package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.forum.AdminForumCommentResponse;
import com.back_end.english_app.dto.respones.forum.AdminForumPostResponse;
import com.back_end.english_app.dto.respones.forum.AdminForumStatisticsResponse;
import com.back_end.english_app.entity.ForumCommentEntity;
import com.back_end.english_app.entity.ForumPostEntity;
import com.back_end.english_app.entity.ForumPostViewEntity;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminForumService {

    private final ForumPostRepository forumPostRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final ForumLikeRepository forumLikeRepository;
    private final ForumPostViewRepository forumPostViewRepository;
    private final ForumPostMediaRepository forumPostMediaRepository;

    // ==================== POST MANAGEMENT ====================

    /**
     * Get all forum posts with pagination and filters
     */
    public Page<AdminForumPostResponse> getAllPosts(int page, int size, Boolean isActive, String search) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ForumPostEntity> postPage;

        if (search != null && !search.trim().isEmpty()) {
            if (isActive != null) {
                postPage = forumPostRepository.findByIsActiveAndTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                        isActive, search, search, pageable);
            } else {
                postPage = forumPostRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
                        search, search, pageable);
            }
        } else {
            if (isActive != null) {
                postPage = forumPostRepository.findByIsActive(isActive, pageable);
            } else {
                postPage = forumPostRepository.findAll(pageable);
            }
        }

        return postPage.map(this::convertToAdminPostResponse);
    }

    /**
     * Delete or restore a post
     */
    @Transactional
    public String deleteOrRestorePost(Long postId, String action) {
        ForumPostEntity post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if ("delete".equalsIgnoreCase(action)) {
            post.setIsActive(false);
            post.setUpdatedAt(LocalDateTime.now());
            forumPostRepository.save(post);
            log.info("Post {} has been deleted by admin", postId);
            return "Post deleted successfully";
        } else if ("restore".equalsIgnoreCase(action)) {
            post.setIsActive(true);
            post.setUpdatedAt(LocalDateTime.now());
            forumPostRepository.save(post);
            log.info("Post {} has been restored by admin", postId);
            return "Post restored successfully";
        } else {
            throw new IllegalArgumentException("Invalid action. Use 'delete' or 'restore'");
        }
    }

    // ==================== COMMENT MANAGEMENT ====================

    /**
     * Get all comments with pagination and filters
     */
    public Page<AdminForumCommentResponse> getAllComments(int page, int size, Boolean isActive, Long postId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ForumCommentEntity> commentPage;

        if (postId != null) {
            if (isActive != null) {
                commentPage = forumCommentRepository.findByPostIdAndIsActive(postId, isActive, pageable);
            } else {
                commentPage = forumCommentRepository.findByPostId(postId, pageable);
            }
        } else {
            if (isActive != null) {
                commentPage = forumCommentRepository.findByIsActive(isActive, pageable);
            } else {
                commentPage = forumCommentRepository.findAll(pageable);
            }
        }

        return commentPage.map(this::convertToAdminCommentResponse);
    }

    /**
     * Delete or restore a comment
     */
    @Transactional
    public String deleteOrRestoreComment(Long commentId, String action) {
        ForumCommentEntity comment = forumCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        if ("delete".equalsIgnoreCase(action)) {
            comment.setIsActive(false);
            comment.setUpdatedAt(LocalDateTime.now());
            
            // Update comment count in post
            ForumPostEntity post = comment.getPost();
            if (post.getCommentsCount() > 0) {
                post.setCommentsCount(post.getCommentsCount() - 1);
                forumPostRepository.save(post);
            }
            
            forumCommentRepository.save(comment);
            log.info("Comment {} has been deleted by admin", commentId);
            return "Comment deleted successfully";
        } else if ("restore".equalsIgnoreCase(action)) {
            comment.setIsActive(true);
            comment.setUpdatedAt(LocalDateTime.now());
            
            // Update comment count in post
            ForumPostEntity post = comment.getPost();
            post.setCommentsCount(post.getCommentsCount() + 1);
            forumPostRepository.save(post);
            
            forumCommentRepository.save(comment);
            log.info("Comment {} has been restored by admin", commentId);
            return "Comment restored successfully";
        } else {
            throw new IllegalArgumentException("Invalid action. Use 'delete' or 'restore'");
        }
    }

    // ==================== STATISTICS ====================

    /**
     * Get forum statistics for admin dashboard
     */
    public AdminForumStatisticsResponse getStatistics() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        Long totalPosts = forumPostRepository.count();
        Long totalActivePosts = forumPostRepository.countByIsActive(true);
        Long totalDeletedPosts = totalPosts - totalActivePosts;

        Long totalComments = forumCommentRepository.count();
        Long totalActiveComments = forumCommentRepository.countByIsActive(true);
        Long totalDeletedComments = totalComments - totalActiveComments;

        Long totalLikes = forumLikeRepository.count();
        Long totalViews = forumPostViewRepository.count();

        Long postsToday = forumPostRepository.countByCreatedAtBetween(todayStart, todayEnd);
        Long commentsToday = forumCommentRepository.countByCreatedAtBetween(todayStart, todayEnd);

        return AdminForumStatisticsResponse.builder()
                .totalPosts(totalPosts)
                .totalActivePosts(totalActivePosts)
                .totalDeletedPosts(totalDeletedPosts)
                .totalComments(totalComments)
                .totalActiveComments(totalActiveComments)
                .totalDeletedComments(totalDeletedComments)
                .totalLikes(totalLikes)
                .totalViews(totalViews)
                .postsToday(postsToday)
                .commentsToday(commentsToday)
                .build();
    }

    // ==================== HELPER METHODS ====================

    private AdminForumPostResponse convertToAdminPostResponse(ForumPostEntity post) {
        Integer viewCount = forumPostViewRepository.countByPostId(post.getId());
        Integer mediaCount = forumPostMediaRepository.countByPostId(post.getId());

        return AdminForumPostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .userFullname(post.getUser().getFullname())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .viewCount(viewCount)
                .xpReward(post.getXpReward())
                .isActive(post.getIsActive())
                .hasMedia(mediaCount > 0)
                .mediaCount(mediaCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private AdminForumCommentResponse convertToAdminCommentResponse(ForumCommentEntity comment) {
        Integer repliesCount = comment.getReplies() != null ? comment.getReplies().size() : 0;

        return AdminForumCommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .postTitle(comment.getPost().getTitle())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .userFullname(comment.getUser().getFullname())
                .content(comment.getContent())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .likesCount(comment.getLikesCount())
                .repliesCount(repliesCount)
                .isActive(comment.getIsActive())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

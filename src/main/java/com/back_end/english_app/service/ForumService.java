package com.back_end.english_app.service;

import com.back_end.english_app.dto.request.forum.CreateCommentRequest;
import com.back_end.english_app.dto.request.forum.CreatePostRequest;
import com.back_end.english_app.dto.request.forum.UpdateCommentRequest;
import com.back_end.english_app.dto.request.forum.UpdatePostRequest;
import com.back_end.english_app.dto.respones.forum.*;
import com.back_end.english_app.entity.*;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ForumService {

    private final ForumPostRepository forumPostRepository;
    private final ForumCommentRepository forumCommentRepository;
    private final ForumLikeRepository forumLikeRepository;
    private final ForumPostMediaRepository forumPostMediaRepository;
    private final ForumPostViewRepository forumPostViewRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final BadgeCheckService badgeCheckService;
    private final UserDailyStatsService userDailyStatsService;

    // ============ POST METHODS ============

    @Transactional
    public PostListResponse getPosts(int page, int limit, String search, String dateFilter, Long filterUserId, Long currentUserId) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<ForumPostEntity> postPage;

        LocalDateTime filterDate = getFilterDate(dateFilter);

        // Filter by userId if provided
        if (filterUserId != null) {
            if (search != null && !search.trim().isEmpty()) {
                if (filterDate != null) {
                    postPage = forumPostRepository.searchPostsByUserIdWithDateFilter(filterUserId, search, filterDate, pageable);
                } else {
                    postPage = forumPostRepository.searchPostsByUserId(filterUserId, search, pageable);
                }
            } else {
                if (filterDate != null) {
                    postPage = forumPostRepository.findByUserIdAndCreatedAtAfter(filterUserId, filterDate, pageable);
                } else {
                    postPage = forumPostRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(filterUserId, pageable);
                }
            }
        } else {
            // No userId filter - get all posts
            if (search != null && !search.trim().isEmpty()) {
                if (filterDate != null) {
                    postPage = forumPostRepository.searchPostsWithDateFilter(search, filterDate, pageable);
                } else {
                    postPage = forumPostRepository.searchPosts(search, pageable);
                }
            } else {
                if (filterDate != null) {
                    postPage = forumPostRepository.findByCreatedAtAfter(filterDate, pageable);
                } else {
                    postPage = forumPostRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
                }
            }
        }

        List<PostResponse> posts = postPage.getContent().stream()
                .map(post -> convertToPostResponse(post, currentUserId))
                .collect(Collectors.toList());

        return PostListResponse.builder()
                .posts(posts)
                .currentPage(page)
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .pageSize(limit)
                .build();
    }

    @Transactional
    public PostResponse getPostById(Long postId, Long currentUserId, HttpServletRequest request) {
        ForumPostEntity post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        if (!post.getIsActive()) {
            throw new ResourceNotFoundException("Post not found or has been deleted");
        }

        // Track view
        trackPostView(postId, currentUserId, getClientIp(request));

        return convertToPostResponse(post, currentUserId);
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request, Long userId) throws IOException {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create post
        ForumPostEntity post = new ForumPostEntity();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setTags(request.getTags());
        post.setLikesCount(0);
        post.setCommentsCount(0);
        post.setIsActive(true);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        post = forumPostRepository.save(post);

        // Upload and save media
        List<ForumPostMediaEntity> mediaList = new ArrayList<>();

        if (request.getImages() != null) {
            mediaList.addAll(uploadMedia(request.getImages(), post, ForumPostMediaEntity.MediaType.image));
        }

        if (request.getFiles() != null) {
            mediaList.addAll(uploadMedia(request.getFiles(), post, ForumPostMediaEntity.MediaType.file));
        }

        post.setMedia(mediaList);

        // Cập nhật daily stats
        userDailyStatsService.recordForumPost(user, 1);

        // Check forum badges
        try {
            badgeCheckService.checkAndUpdateBadges(userId, "FORUM");
            log.info("Badge check completed for user {} after creating forum post", userId);
        } catch (Exception e) {
            log.error("Error checking badges for user {}: {}", userId, e.getMessage(), e);
            // Không throw exception để không ảnh hưởng đến flow chính
        }

        return convertToPostResponse(post, userId);
    }

    @Transactional
    public PostResponse updatePost(Long postId, UpdatePostRequest request, Long userId) throws IOException {
        ForumPostEntity post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to edit this post");
        }

        // Update content
        post.setContent(request.getContent());

        // Update tags if provided
        if (request.getTags() != null) {
            post.setTags(request.getTags());
        }

        post.setUpdatedAt(LocalDateTime.now());

        // Handle media removal
        if (request.getRemovedMediaIds() != null && !request.getRemovedMediaIds().isEmpty()) {
            List<ForumPostMediaEntity> mediaToRemove = forumPostMediaRepository
                    .findByIdInAndIsActiveTrue(request.getRemovedMediaIds());

            for (ForumPostMediaEntity media : mediaToRemove) {
                media.setIsActive(false);
                try {
                    fileUploadService.deleteFile(media.getUrl());
                } catch (IOException e) {
                    // Log error but continue
                }
            }
            forumPostMediaRepository.saveAll(mediaToRemove);
        }

        // Upload new media
        List<ForumPostMediaEntity> newMedia = new ArrayList<>();

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            newMedia.addAll(uploadMedia(request.getImages(), post, ForumPostMediaEntity.MediaType.image));
        }

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            newMedia.addAll(uploadMedia(request.getFiles(), post, ForumPostMediaEntity.MediaType.file));
        }

        forumPostRepository.save(post);

        return convertToPostResponse(post, userId);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        ForumPostEntity post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to delete this post");
        }

        post.setIsActive(false);
        forumPostRepository.save(post);
    }

    // ============ COMMENT METHODS ============

    @Transactional
    public CommentListResponse getComments(Long postId, int page, int limit, Long currentUserId) {
        // Verify post exists
        forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<ForumCommentEntity> commentPage = forumCommentRepository
                .findRootCommentsByPostId(postId, pageable);

        List<CommentResponse> comments = commentPage.getContent().stream()
                .map(comment -> convertToCommentResponse(comment, currentUserId))
                .collect(Collectors.toList());

        return CommentListResponse.builder()
                .comments(comments)
                .currentPage(page)
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .pageSize(limit)
                .build();
    }

    @Transactional
    public CommentResponse createComment(Long postId, CreateCommentRequest request, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ForumPostEntity post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        ForumCommentEntity comment = new ForumCommentEntity();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent());
        comment.setLikesCount(0);
        comment.setIsActive(true);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        // Handle parent comment (reply)
        if (request.getParentId() != null) {
            ForumCommentEntity parent = forumCommentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParent(parent);
        }

        comment = forumCommentRepository.save(comment);

        // Cập nhật daily stats
        userDailyStatsService.recordForumComment(user, 1);

        // Update post comment count
        post.setCommentsCount(post.getCommentsCount() + 1);
        forumPostRepository.save(post);

        return convertToCommentResponse(comment, userId);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long userId) {
        ForumCommentEntity comment = forumCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to edit this comment");
        }

        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        comment = forumCommentRepository.save(comment);

        return convertToCommentResponse(comment, userId);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        ForumCommentEntity comment = forumCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You don't have permission to delete this comment");
        }

        comment.setIsActive(false);
        forumCommentRepository.save(comment);

        // Update post comment count
        ForumPostEntity post = comment.getPost();
        post.setCommentsCount(Math.max(0, post.getCommentsCount() - 1));
        forumPostRepository.save(post);
    }

    // ============ LIKE METHODS ============

    @Transactional
    public void togglePostLike(Long postId, Long userId) {
        ForumPostEntity post = forumPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean exists = forumLikeRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId, ForumLikeEntity.TargetType.post, postId);

        if (exists) {
            // Unlike
            forumLikeRepository.deleteByUserIdAndTargetTypeAndTargetId(
                    userId, ForumLikeEntity.TargetType.post, postId);
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
        } else {
            // Like
            ForumLikeEntity like = new ForumLikeEntity();
            like.setUser(user);
            like.setTargetType(ForumLikeEntity.TargetType.post);
            like.setTargetId(postId);
            like.setCreatedAt(LocalDateTime.now());
            forumLikeRepository.save(like);

            post.setLikesCount(post.getLikesCount() + 1);
        }

        forumPostRepository.save(post);
    }

    @Transactional
    public void toggleCommentLike(Long commentId, Long userId) {
        ForumCommentEntity comment = forumCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean exists = forumLikeRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId, ForumLikeEntity.TargetType.comment, commentId);

        if (exists) {
            // Unlike
            forumLikeRepository.deleteByUserIdAndTargetTypeAndTargetId(
                    userId, ForumLikeEntity.TargetType.comment, commentId);
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
        } else {
            // Like
            ForumLikeEntity like = new ForumLikeEntity();
            like.setUser(user);
            like.setTargetType(ForumLikeEntity.TargetType.comment);
            like.setTargetId(commentId);
            like.setCreatedAt(LocalDateTime.now());
            forumLikeRepository.save(like);

            comment.setLikesCount(comment.getLikesCount() + 1);
        }

        forumCommentRepository.save(comment);
    }

    // ============ HELPER METHODS ============

    private void trackPostView(Long postId, Long userId, String ipAddress) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        boolean recentView;
        if (userId != null) {
            recentView = forumPostViewRepository.existsByPostIdAndUserIdAndViewedAtAfter(
                    postId, userId, oneHourAgo);
        } else {
            recentView = forumPostViewRepository.existsByPostIdAndIpAddressAndViewedAtAfter(
                    postId, ipAddress, oneHourAgo);
        }

        if (!recentView) {
            ForumPostEntity post = forumPostRepository.findById(postId).orElseThrow();
            UserEntity user = userId != null ? userRepository.findById(userId).orElse(null) : null;

            ForumPostViewEntity view = new ForumPostViewEntity();
            view.setPost(post);
            view.setUser(user);
            view.setIpAddress(ipAddress);
            view.setViewedAt(LocalDateTime.now());

            forumPostViewRepository.save(view);
        }
    }

    private List<ForumPostMediaEntity> uploadMedia(List<MultipartFile> files, ForumPostEntity post,
                                                   ForumPostMediaEntity.MediaType mediaType) throws IOException {
        List<ForumPostMediaEntity> mediaList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            // Lưu vào forum/images hoặc forum/files để khớp với URL FE gọi
            String subDir = mediaType == ForumPostMediaEntity.MediaType.image ? "forum/images" : "forum/files";
            String url = fileUploadService.uploadFile(file, subDir);

            ForumPostMediaEntity media = new ForumPostMediaEntity();
            media.setPost(post);
            media.setMediaType(mediaType);
            media.setFileName(file.getOriginalFilename());
            media.setMimeType(file.getContentType());
            media.setFileSize((int) file.getSize());
            media.setUrl(url);
            media.setIsActive(true);
            media.setCreatedAt(LocalDateTime.now());

            mediaList.add(forumPostMediaRepository.save(media));
        }

        return mediaList;
    }

    private PostResponse convertToPostResponse(ForumPostEntity post, Long currentUserId) {
        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = forumLikeRepository.existsByUserIdAndTargetTypeAndTargetId(
                    currentUserId, ForumLikeEntity.TargetType.post, post.getId());
        }

        List<MediaResponse> mediaResponses = new ArrayList<>();
        if (post.getMedia() != null) {
            mediaResponses = post.getMedia().stream()
                    .filter(ForumPostMediaEntity::getIsActive)
                    .map(this::convertToMediaResponse)
                    .collect(Collectors.toList());
        }

        long viewsCount = forumPostViewRepository.countByPostId(post.getId());

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .userAvatar(post.getUser().getAvatar())
                .title(post.getTitle())
                .content(post.getContent())
                .tags(post.getTags() != null ? post.getTags() : new ArrayList<>())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getCommentsCount())
                .viewsCount((int) viewsCount)
                .isLiked(isLiked)
                .media(mediaResponses)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    private CommentResponse convertToCommentResponse(ForumCommentEntity comment, Long currentUserId) {
        boolean isLiked = false;
        if (currentUserId != null) {
            isLiked = forumLikeRepository.existsByUserIdAndTargetTypeAndTargetId(
                    currentUserId, ForumLikeEntity.TargetType.comment, comment.getId());
        }

        List<CommentResponse> replies = new ArrayList<>();
        if (comment.getReplies() != null) {
            replies = comment.getReplies().stream()
                    .filter(ForumCommentEntity::getIsActive)
                    .map(reply -> convertToCommentResponse(reply, currentUserId))
                    .collect(Collectors.toList());
        }

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .userAvatar(comment.getUser().getAvatar())
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .content(comment.getContent())
                .likesCount(comment.getLikesCount())
                .isLiked(isLiked)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(replies)
                .build();
    }

    private MediaResponse convertToMediaResponse(ForumPostMediaEntity media) {
        // Convert relative path to full URL
        String fullUrl = fileUploadService.buildFullUrl(media.getUrl());
        String fullThumbnailUrl = media.getThumbnailUrl() != null 
                ? fileUploadService.buildFullUrl(media.getThumbnailUrl()) 
                : null;
        
        return MediaResponse.builder()
                .id(media.getId())
                .mediaType(media.getMediaType().name())
                .fileName(media.getFileName())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .url(fullUrl)
                .thumbnailUrl(fullThumbnailUrl)
                .createdAt(media.getCreatedAt())
                .build();
    }

    private LocalDateTime getFilterDate(String dateFilter) {
        if (dateFilter == null || dateFilter.equals("all")) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        return switch (dateFilter.toLowerCase()) {
            case "today" -> now.toLocalDate().atStartOfDay();
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> null;
        };
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public List<RecentPostResponse> getRecentPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        Page<ForumPostEntity> postPage = forumPostRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);

        return postPage.getContent().stream()
                .map(this::convertToRecentPostResponse)
                .collect(Collectors.toList());
    }

    private RecentPostResponse convertToRecentPostResponse(ForumPostEntity post) {
        return RecentPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .author(post.getUser().getUsername())
                .avatar(post.getUser().getAvatar())
                .time(getRelativeTime(post.getCreatedAt()))
                .createdAt(post.getCreatedAt())
                .likes(post.getLikesCount())
                .comments(post.getCommentsCount())
                .build();
    }

    private String getRelativeTime(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1) {
            return "Vừa xong";
        } else if (minutes < 60) {
            return minutes + " phút trước";
        } else if (hours < 24) {
            return hours + " giờ trước";
        } else if (days < 7) {
            return days + " ngày trước";
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks + " tuần trước";
        } else if (days < 365) {
            long months = days / 30;
            return months + " tháng trước";
        } else {
            long years = days / 365;
            return years + " năm trước";
        }
    }
}


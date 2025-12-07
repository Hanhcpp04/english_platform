package com.back_end.english_app.controller.forum;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.forum.CreateCommentRequest;
import com.back_end.english_app.dto.request.forum.CreatePostRequest;
import com.back_end.english_app.dto.request.forum.UpdateCommentRequest;
import com.back_end.english_app.dto.request.forum.UpdatePostRequest;
import com.back_end.english_app.dto.respones.forum.CommentListResponse;
import com.back_end.english_app.dto.respones.forum.CommentResponse;
import com.back_end.english_app.dto.respones.forum.PostListResponse;
import com.back_end.english_app.dto.respones.forum.PostResponse;
import com.back_end.english_app.dto.respones.forum.RecentPostResponse;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.service.ForumService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;
    private final UserRepository userRepository;
    @GetMapping("/posts/recent")
    public APIResponse<List<RecentPostResponse>> getRecentPosts(
            @RequestParam(defaultValue = "10") int limit) {

        List<RecentPostResponse> response = forumService.getRecentPosts(limit);
        return APIResponse.success(response);
    }

    @GetMapping("/posts")
    public APIResponse<PostListResponse> getPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "all") String dateFilter,
            @RequestParam(required = false) Long userId) {

        Long currentUserId = getCurrentUserId();
        PostListResponse response = forumService.getPosts(page, limit, search, dateFilter, userId, currentUserId);
        return APIResponse.success(response);
    }


    @GetMapping("/post/detail/{postId}")
    public APIResponse<PostResponse> getPostById(
            @PathVariable Long postId,
            HttpServletRequest request) {

        Long currentUserId = getCurrentUserId();
        PostResponse response = forumService.getPostById(postId, currentUserId, request);
        return APIResponse.success(response);
    }

    @PostMapping("/post/create/{userId}")
    public APIResponse<PostResponse> createPost(
            @PathVariable Long userId,
            @RequestParam(required = false) String title,
            @RequestParam String content,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) List<MultipartFile> files) throws IOException {

        Optional<UserEntity> user = userRepository.findByIdAndIsActiveTrue(userId);
        if(user.isEmpty()){
            return APIResponse.notFound("Người dùng không tồn tại");
        }

        CreatePostRequest request = new CreatePostRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setTags(tags);
        request.setImages(images);
        request.setFiles(files);

        PostResponse response = forumService.createPost(request, userId);
        return APIResponse.success(response);
    }


    @PutMapping("/post/update/{postId}")
    public APIResponse<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestParam String content,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) List<MultipartFile> images,
            @RequestParam(required = false) List<MultipartFile> files,
            @RequestParam(required = false) List<Long> existingMediaIds,
            @RequestParam(required = false) List<Long> removedMediaIds) throws IOException {
        System.out.println("Đang chạy api cập nhật bài viết bên ! ---------------------------------------------------");
        Long userId = getCurrentUserId();
        if (userId == null) {
            return APIResponse.error("Unauthorized");
        }

        UpdatePostRequest request = new UpdatePostRequest();
        request.setContent(content);
        request.setTags(tags);
        request.setImages(images);
        request.setFiles(files);
        request.setExistingMediaIds(existingMediaIds);
        request.setRemovedMediaIds(removedMediaIds);

        PostResponse response = forumService.updatePost(postId, request, userId);
        return APIResponse.success(response);
    }
    @DeleteMapping("/post/delete/{postId}")
    public APIResponse<String> deletePost(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return APIResponse.error("Unauthorized");
        }

        forumService.deletePost(postId, userId);

        return APIResponse.success("Post deleted successfully");
    }

    // ============ COMMENT ENDPOINTS ============

    @GetMapping("/posts/{postId}/comments")
    public APIResponse<CommentListResponse> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Long currentUserId = getCurrentUserId();
        CommentListResponse response = forumService.getComments(postId, page, limit, currentUserId);
        return APIResponse.success(response);
    }

    @PostMapping("/posts/{postId}/comments")
    public APIResponse<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody CreateCommentRequest request) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            return APIResponse.error("Unauthorized");
        }

        CommentResponse response = forumService.createComment(postId, request, userId);
        return APIResponse.success(response);
    }
    @PutMapping("/comments/{commentId}")
    public APIResponse<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request) {

        Long userId = getCurrentUserId();
        if (userId == null) {
            return APIResponse.error("Unauthorized");
        }

        CommentResponse response = forumService.updateComment(commentId, request, userId);
        return APIResponse.success(response);
    }
    @DeleteMapping("/comments/{commentId}")
    public APIResponse<String> deleteComment(@PathVariable Long commentId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return APIResponse.error("Unauthorized");
        }

        forumService.deleteComment(commentId, userId);

        return APIResponse.success("Comment deleted successfully");
    }
    @PostMapping("/posts/{postId}/like")
    public APIResponse<String> togglePostLike(@PathVariable Long postId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return APIResponse.error("Unauthorized");
        }

        forumService.togglePostLike(postId, userId);

        return APIResponse.success("Like toggled successfully");
    }

    @PostMapping("/comments/{commentId}/like")
    public APIResponse<String> toggleCommentLike(@PathVariable Long commentId) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return APIResponse.error("Unauthorized");
        }

        forumService.toggleCommentLike(commentId, userId);

        return APIResponse.success("Like toggled successfully");
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        // authentication.getName() trả về email, cần tìm userId từ email
        String email = authentication.getName();
        Optional<UserEntity> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
        return userOpt.map(UserEntity::getId).orElse(null);
    }
}


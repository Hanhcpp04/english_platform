package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.forum.AdminForumCommentResponse;
import com.back_end.english_app.dto.respones.forum.AdminForumPostResponse;
import com.back_end.english_app.dto.respones.forum.AdminForumStatisticsResponse;
import com.back_end.english_app.service.admin.AdminForumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-forum")
@RequiredArgsConstructor
@Slf4j
public class AdminForumController {

    private final AdminForumService adminForumService;

    /**
     * Get all forum posts with pagination and filters
     * GET /api/v1/admin-forum/posts
     */
    @GetMapping("/posts")
    public ResponseEntity<APIResponse<Page<AdminForumPostResponse>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String search) {
        
        Page<AdminForumPostResponse> posts = adminForumService.getAllPosts(page, size, isActive, search);
        
        APIResponse<Page<AdminForumPostResponse>> response = APIResponse.<Page<AdminForumPostResponse>>builder()
                .code(1000)
                .result(posts)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete or restore a post
     * PUT /api/v1/admin-forum/posts/{postId}/{action}
     */
    @PutMapping("/posts/{postId}/{action}")
    public ResponseEntity<APIResponse<String>> deleteOrRestorePost(
            @PathVariable Long postId,
            @PathVariable String action) {
        
        String message = adminForumService.deleteOrRestorePost(postId, action);
        
        APIResponse<String> response = APIResponse.<String>builder()
                .code(1000)
                .result(message)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get all comments with pagination and filters
     * GET /api/v1/admin-forum/comments
     */
    @GetMapping("/comments")
    public ResponseEntity<APIResponse<Page<AdminForumCommentResponse>>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Long postId) {
        
        Page<AdminForumCommentResponse> comments = adminForumService.getAllComments(page, size, isActive, postId);
        
        APIResponse<Page<AdminForumCommentResponse>> response = APIResponse.<Page<AdminForumCommentResponse>>builder()
                .code(1000)
                .result(comments)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Delete or restore a comment
     * PUT /api/v1/admin-forum/comments/{commentId}/{action}
     */
    @PutMapping("/comments/{commentId}/{action}")
    public ResponseEntity<APIResponse<String>> deleteOrRestoreComment(
            @PathVariable Long commentId,
            @PathVariable String action) {
        
        String message = adminForumService.deleteOrRestoreComment(commentId, action);
        
        APIResponse<String> response = APIResponse.<String>builder()
                .code(1000)
                .result(message)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get forum statistics
     * GET /api/v1/admin-forum/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<APIResponse<AdminForumStatisticsResponse>> getStatistics() {
        AdminForumStatisticsResponse statistics = adminForumService.getStatistics();
        
        APIResponse<AdminForumStatisticsResponse> response = APIResponse.<AdminForumStatisticsResponse>builder()
                .code(1000)
                .result(statistics)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

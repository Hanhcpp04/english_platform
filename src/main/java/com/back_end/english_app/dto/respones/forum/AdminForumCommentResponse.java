package com.back_end.english_app.dto.respones.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminForumCommentResponse {
    private Long id;
    private Long postId;
    private String postTitle;
    private Long userId;
    private String username;
    private String userFullname;
    private String content;
    private Long parentId;
    private Integer likesCount;
    private Integer repliesCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.back_end.english_app.dto.respones.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private Long id;
    private Long userId;
    private String username;
    private String userAvatar;
    private String title;
    private String content;
    private List<String> tags;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer viewsCount;
    private Boolean isLiked;
    private List<MediaResponse> media;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


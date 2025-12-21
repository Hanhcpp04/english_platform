package com.back_end.english_app.dto.respones.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminForumPostResponse {
    private Long id;
    private Long userId;
    private String username;
    private String userFullname;
    private String title;
    private String content;
    private List<String> tags;
    private Integer likesCount;
    private Integer commentsCount;
    private Integer viewCount;
    private Integer xpReward;
    private Boolean isActive;
    private Boolean hasMedia;
    private Integer mediaCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

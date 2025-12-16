package com.back_end.english_app.dto.respones.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentPostResponse {
    private Long id;
    private String title;
    private String author;
    private String avatar;
    private String time;
    private LocalDateTime createdAt;
    private Integer likes;
    private Integer comments;
}


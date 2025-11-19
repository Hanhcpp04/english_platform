package com.back_end.english_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarLessonDTO {
    private Long id;
    private String title;
    private String content;
    private Integer xpReward;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Boolean isCompleted;
}


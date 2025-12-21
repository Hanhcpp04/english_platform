package com.back_end.english_app.dto.respones.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminGrammarTopicResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private Integer xpReward;
    private Integer totalLessons;
    private Integer totalExercises;
    private LocalDateTime createdAt;
}

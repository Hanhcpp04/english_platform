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
public class AdminGrammarLessonResponse {
    private Long id;
    private Long topicId;
    private String topicName;
    private String title;
    private String content;
    private Integer xpReward;
    private Boolean isActive;
    private Integer totalQuestions;
    private LocalDateTime createdAt;
}

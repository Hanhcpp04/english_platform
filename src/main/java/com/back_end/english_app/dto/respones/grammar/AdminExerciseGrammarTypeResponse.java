package com.back_end.english_app.dto.respones.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminExerciseGrammarTypeResponse {
    private Long id;
    private Long topicId;
    private String topicName;
    private String name;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Integer totalQuestions;
}

package com.back_end.english_app.dto.respones.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabExerciseTypeResponse {
    private Long id;
    private String name;
    private String description;
    private String instruction;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Integer totalQuestions;
}

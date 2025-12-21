package com.back_end.english_app.dto.respones.vocabExercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitBatchResponse {
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer score;
    private Integer xpEarned;
    private Integer totalXp;
    private Boolean completedExerciseType;
}
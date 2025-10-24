package com.back_end.english_app.dto.respones.vocabExercise;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class BatchAnswerItem {
    @NotNull
    private Integer questionId;
    @NotNull
    private String userAnswer;
    @NotNull
    private Boolean isCorrect;
}

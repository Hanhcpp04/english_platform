package com.back_end.english_app.dto.request.vocabExercise;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class SubmitAnswerRequest {
    @NotNull
    private Integer userId;
    @NotNull
    private String userAnswer;
    @NotNull
    private String exerciseType;
    @NotNull
    private Integer typeId;
    @NotNull
    private Integer topicId;
}

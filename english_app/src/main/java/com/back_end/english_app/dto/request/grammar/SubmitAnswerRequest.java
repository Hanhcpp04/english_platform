package com.back_end.english_app.dto.request.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerRequest {

    @NotNull(message = "User ID is required")
    @JsonProperty("user_id")
    private Long userId;

    @NotNull(message = "Question ID is required")
    @JsonProperty("question_id")
    private Long questionId;

    @NotNull(message = "Type ID is required")
    @JsonProperty("type_id")
    private Long typeId;

    @NotNull(message = "User answer is required")
    @JsonProperty("user_answer")
    private String userAnswer;
}


package com.back_end.english_app.dto.respones.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerResponseDTO {

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("xp_earned")
    private Integer xpEarned;

    private String message;
}


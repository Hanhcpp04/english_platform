package com.back_end.english_app.dto.respones.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerDTO {

    @JsonProperty("question_id")
    private Long questionId;

    private String question;

    @JsonProperty("user_answer")
    private String userAnswer;

    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    @JsonProperty("attempted_at")
    private LocalDateTime attemptedAt;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("xp_reward")
    private Integer xpReward;
}



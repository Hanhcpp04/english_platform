package com.back_end.english_app.dto.respones.vocabExercise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho lịch sử câu trả lời từng câu hỏi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabAnswerHistoryDTO {

    @JsonProperty("question_id")
    private Long questionId;

    private String question;

    @JsonProperty("user_answer")
    private String userAnswer;

    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    @JsonProperty("xp_earned")
    private Integer xpEarned;

    @JsonProperty("attempted_at")
    private LocalDateTime attemptedAt;
}



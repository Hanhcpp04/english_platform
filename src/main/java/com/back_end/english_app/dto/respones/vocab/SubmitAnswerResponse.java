package com.back_end.english_app.dto.respones.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAnswerResponse {
    private Boolean isCorrect;
    private String correctAnswer;
    private String userAnswer;
    private Integer xpEarned;
    private Integer totalXp;
    private String explanation;
    private ExerciseQuestionResponse nextQuestion;
}


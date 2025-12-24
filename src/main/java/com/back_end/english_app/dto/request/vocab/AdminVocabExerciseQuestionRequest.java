package com.back_end.english_app.dto.request.vocab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabExerciseQuestionRequest {
    private Long typeId;
    private Long topicId;
    private String question;
    private String options; // JSON string
    private String correctAnswer;
    private String explanation;
    private Integer xpReward = 5;
    private Boolean isActive = true;
}

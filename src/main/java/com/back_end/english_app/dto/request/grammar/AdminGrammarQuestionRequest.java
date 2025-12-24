package com.back_end.english_app.dto.request.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminGrammarQuestionRequest {
    private Long lessonId;
    private Long typeId;
    private String question;
    private String options; // JSON string
    private String correctAnswer;
    private Integer xpReward = 5;
    private Boolean isActive = true;
}

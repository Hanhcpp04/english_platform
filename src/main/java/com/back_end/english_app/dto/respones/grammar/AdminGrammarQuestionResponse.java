package com.back_end.english_app.dto.respones.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminGrammarQuestionResponse {
    private Long id;
    private Long lessonId;
    private String lessonTitle;
    private Long typeId;
    private String typeName;
    private String question;
    private String options; // JSON string
    private String correctAnswer;
    private Integer xpReward;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

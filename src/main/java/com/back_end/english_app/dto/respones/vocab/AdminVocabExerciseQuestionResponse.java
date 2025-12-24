package com.back_end.english_app.dto.respones.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabExerciseQuestionResponse {
    private Long id;
    private Long typeId;
    private String typeName;
    private Long topicId;
    private String topicName;
    private String question;
    private String options; // JSON string
    private String correctAnswer;
    private String explanation;
    private Integer xpReward;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

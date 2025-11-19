package com.back_end.english_app.dto.respones.writing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritingPromptResponse {
    private Integer id;
    private Integer taskId;
    private String mode;
    private String userContent;
    private Integer wordCount;
    private Integer grammarScore;
    private Integer vocabularyScore;
    private Integer coherenceScore;
    private Integer overallScore;
    private String aiFeedback;
    private String grammarSuggestions;
    private String vocabularySuggestions;
    private Integer timeSpent;
    private Integer xpReward;
    private Boolean isCompleted;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
}


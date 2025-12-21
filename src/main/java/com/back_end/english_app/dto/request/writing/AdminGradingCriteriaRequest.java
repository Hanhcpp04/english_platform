package com.back_end.english_app.dto.request.writing;

import lombok.Data;

@Data
public class AdminGradingCriteriaRequest {
    private Integer grammarWeight = 30;
    private Integer vocabularyWeight = 30;
    private Integer coherenceWeight = 40;
    private Integer minWordCount = 100;
    private Integer maxWordCount = 500;
    private String customInstructions;
}

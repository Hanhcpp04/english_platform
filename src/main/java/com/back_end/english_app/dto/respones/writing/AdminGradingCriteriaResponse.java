package com.back_end.english_app.dto.respones.writing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminGradingCriteriaResponse {
    private Integer id;
    private Integer taskId;
    private Integer grammarWeight;
    private Integer vocabularyWeight;
    private Integer coherenceWeight;
    private Integer minWordCount;
    private Integer maxWordCount;
    private String customInstructions;
    private String updatedAt;
}

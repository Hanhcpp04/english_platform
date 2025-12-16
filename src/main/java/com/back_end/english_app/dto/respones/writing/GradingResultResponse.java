package com.back_end.english_app.dto.respones.writing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingResultResponse {

    private Integer promptId;
    
    // Scores (0-100)
    private Integer grammarScore;
    private Integer vocabularyScore;
    private Integer coherenceScore;
    private Integer overallScore;
    
    // Detailed feedback
    private String generalFeedback;
    
    // Grammar suggestions
    private List<GrammarSuggestion> grammarSuggestions;
    
    // Vocabulary suggestions
    private List<VocabularySuggestion> vocabularySuggestions;
    
    // Statistics
    private Integer wordCount;
    private Integer xpEarned;
    private Boolean isCompleted;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GrammarSuggestion {
        private String error;
        private String suggestion;
        private String explanation;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VocabularySuggestion {
        private String word;
        private String betterAlternative;
        private String reason;
    }
}

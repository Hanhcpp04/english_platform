package com.back_end.english_app.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Retention & Drop-off Analysis
 * Funnel analysis for vocabulary topics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetentionAnalysisDTO {
    // Topic Information
    private Integer topicId;
    private String topicName;
    private Integer totalWords;
    
    // Engagement Metrics
    private Long usersStarted;
    private Long usersCompleted;
    private Double completionRate; // Percentage
    private Double dropOffRate; // Percentage
    
    // Progress Metrics
    private Double averageWordsCompleted;
    private Double averageCompletionTime; // in days
    
    // Ranking
    private Integer rank; // Rank by completion rate
}

package com.back_end.english_app.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Writing Analysis Report
 * Contains detailed writing metrics and AI scores
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritingAnalysisDTO {
    private Integer writingId;
    private Long userId;
    private String username;
    
    // Writing Details
    private String mode; // PROMPT or FREE
    private String title;
    private Integer wordCount;
    private Integer timeSpent; // in minutes
    
    // AI Scoring
    private Integer grammarScore;
    private Integer vocabularyScore;
    private Integer coherenceScore;
    private Integer overallScore;
    
    // Feedback
    private String aiFeedback;
    
    // Status
    private Boolean isCompleted;
    private LocalDateTime submittedAt;
    private LocalDateTime createdAt;
    
    // Additional Metrics
    private Integer xpReward;
}

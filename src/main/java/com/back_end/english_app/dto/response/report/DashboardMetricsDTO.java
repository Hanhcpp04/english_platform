package com.back_end.english_app.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for Dashboard Metrics
 * System-wide overview statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsDTO {
    // User Metrics
    private Long totalUsers;
    private Long activeUsers;
    private Long inactiveUsers;
    private Double churnRate; // Percentage of users inactive > 30 days
    
    // Level Distribution
    private Integer levelNumber;
    private String levelName;
    private Long userCount;
    
    // Activity Metrics
    private Long totalVocabCompleted;
    private Long totalGrammarCompleted;
    private Long totalWritingSubmitted;
    private Long totalForumPosts;
    
    // Gamification
    private Long totalXpSystemWide;
    private Long totalBadgesEarned;
    
    // Study Metrics
    private Double averageStudyTimeMinutes;
    private Double averageXpPerUser;
    private Integer totalStudyDays;
    
    // Date for time-series data
    private LocalDate date;
    private Long dailyActiveUsers;
    private Long newUsersToday;
}

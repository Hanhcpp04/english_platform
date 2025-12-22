package com.back_end.english_app.dto.response.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for User Performance Report
 * Combines data from users, user_streaks, level, and badge counts
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPerformanceDTO {
    private Long userId;
    private String username;
    private String email;
    private String fullname;
    private String role;
    
    // Level Information
    private Integer levelNumber;
    private String levelName;
    private Integer totalXp;
    
    // Streak Information
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActivityDate;
    private Integer totalStudyDays;
    
    // Badge Information
    private Long badgeCount;
    
    // Activity Metrics
    private Long vocabCompleted;
    private Long grammarCompleted;
    private Long writingSubmitted;
    private Long forumPosts;
    
    // Status
    private Boolean isActive;
    private LocalDate createdAt;
}

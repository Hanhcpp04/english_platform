package com.back_end.english_app.repository;

import com.back_end.english_app.dto.response.report.DashboardMetricsDTO;
import com.back_end.english_app.dto.response.report.RetentionAnalysisDTO;
import com.back_end.english_app.dto.response.report.UserPerformanceDTO;
import com.back_end.english_app.dto.response.report.WritingAnalysisDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Custom repository for Excel report queries
 * Optimized queries to avoid N+1 problems
 */
@Repository
public interface ReportRepository extends JpaRepository<com.back_end.english_app.entity.UserEntity, Long> {
    
    /**
     * Get comprehensive user performance data with joins (Limited to 5000 users to prevent temp space overflow)
     * Optimized query with explicit LIMIT to avoid MySQL temporary disk space issues
     */
    @Query(value = """
        SELECT 
            u.id,
            u.username,
            u.email,
            u.fullname,
            CAST(u.role AS CHAR) as role,
            l.level_number,
            l.level_name,
            u.total_xp,
            COALESCE(s.current_streak, 0) as current_streak,
            COALESCE(s.longest_streak, 0) as longest_streak,
            s.last_activity_date,
            COALESCE(s.total_study_days, 0) as total_study_days,
            COUNT(DISTINCT ub.id) as badge_count,
            COALESCE(SUM(CASE WHEN vp.is_completed = 1 THEN 1 ELSE 0 END), 0) as vocab_completed,
            COALESCE(SUM(CASE WHEN gp.is_completed = 1 THEN 1 ELSE 0 END), 0) as grammar_completed,
            COALESCE(SUM(CASE WHEN wp.is_completed = 1 THEN 1 ELSE 0 END), 0) as writing_submitted,
            COUNT(DISTINCT fp.id) as forum_posts,
            u.is_active,
            CAST(u.created_at AS DATE) as created_at
        FROM users u
        LEFT JOIN level l ON u.total_xp BETWEEN l.min_xp AND COALESCE(l.max_xp, 999999999)
        LEFT JOIN user_streaks s ON s.user_id = u.id
        LEFT JOIN user_badges ub ON ub.user_id = u.id
        LEFT JOIN vocab_user_progress vp ON vp.user_id = u.id
        LEFT JOIN user_grammar_progress gp ON gp.user_id = u.id
        LEFT JOIN writing_prompts wp ON wp.user_id = u.id
        LEFT JOIN forum_posts fp ON fp.user_id = u.id
        WHERE (:startDate IS NULL OR u.created_at >= :startDate)
          AND (:endDate IS NULL OR u.created_at <= :endDate)
        GROUP BY u.id, u.username, u.email, u.fullname, u.role, 
                 l.level_number, l.level_name, u.total_xp,
                 s.current_streak, s.longest_streak, s.last_activity_date, s.total_study_days,
                 u.is_active, u.created_at
        ORDER BY u.total_xp DESC
        LIMIT 5000
    """, nativeQuery = true)
    List<Object[]> getUserPerformanceReportRaw(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Optimized method that converts raw results to DTOs
     */
    default List<UserPerformanceDTO> getUserPerformanceReport(
        LocalDateTime startDate, 
        LocalDateTime endDate
    ) {
        List<Object[]> rawResults = getUserPerformanceReportRaw(startDate, endDate);
        return rawResults.stream()
            .map(row -> new UserPerformanceDTO(
                ((Number) row[0]).longValue(),      // userId
                (String) row[1],                    // username
                (String) row[2],                    // email
                (String) row[3],                    // fullname
                (String) row[4],                    // role
                row[5] != null ? ((Number) row[5]).intValue() : null,  // levelNumber
                (String) row[6],                    // levelName
                row[7] != null ? ((Number) row[7]).intValue() : null,  // totalXp
                ((Number) row[8]).intValue(),       // currentStreak
                ((Number) row[9]).intValue(),       // longestStreak
                row[10] != null ? ((java.sql.Date) row[10]).toLocalDate() : null,  // lastActivityDate
                ((Number) row[11]).intValue(),      // totalStudyDays
                ((Number) row[12]).longValue(),     // badgeCount
                ((Number) row[13]).longValue(),     // vocabCompleted
                ((Number) row[14]).longValue(),     // grammarCompleted
                ((Number) row[15]).longValue(),     // writingSubmitted
                ((Number) row[16]).longValue(),     // forumPosts
                (Boolean) row[17],                  // isActive
                row[18] != null ? ((java.sql.Date) row[18]).toLocalDate() : null   // createdAt
            ))
            .toList();
    }
    
    /**
     * Get writing analysis data with all scores
     */
    @Query("""
        SELECT new com.back_end.english_app.dto.response.report.WritingAnalysisDTO(
            wp.id,
            wp.user.id,
            u.username,
            CAST(wp.mode AS string),
            wp.writingTask.question,
            wp.wordCount,
            wp.timeSpent,
            wp.grammarScore,
            wp.vocabularyScore,
            wp.coherenceScore,
            wp.overallScore,
            wp.aiFeedback,
            wp.isCompleted,
            wp.submittedAt,
            wp.createdAt,
            wp.xpReward
        )
        FROM WritingPromptEntity wp
        JOIN UserEntity u ON u.id = wp.user.id
        WHERE wp.isCompleted = true
          AND (:startDate IS NULL OR wp.submittedAt >= :startDate)
          AND (:endDate IS NULL OR wp.submittedAt <= :endDate)
        ORDER BY wp.submittedAt DESC
    """)
    List<WritingAnalysisDTO> getWritingAnalysisReport(
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Get retention analysis by vocabulary topics (Funnel Analysis)
     */
    @Query(value = """
        SELECT 
            vt.id as topicId,
            vt.name as topicName,
            vt.total_words as totalWords,
            COUNT(DISTINCT vup.user_id) as usersStarted,
            COUNT(DISTINCT CASE WHEN vup.is_completed = 1 THEN vup.user_id END) as usersCompleted,
            ROUND(COUNT(DISTINCT CASE WHEN vup.is_completed = 1 THEN vup.user_id END) * 100.0 / 
                  NULLIF(COUNT(DISTINCT vup.user_id), 0), 2) as completionRate,
            ROUND(100 - (COUNT(DISTINCT CASE WHEN vup.is_completed = 1 THEN vup.user_id END) * 100.0 / 
                  NULLIF(COUNT(DISTINCT vup.user_id), 0)), 2) as dropOffRate,
            ROUND(AVG(CASE WHEN vup.is_completed = 1 THEN 1 ELSE 0 END) * vt.total_words, 2) as averageWordsCompleted,
            ROUND(AVG(CASE WHEN vup.completed_at IS NOT NULL 
                      THEN DATEDIFF(vup.completed_at, u.created_at) END), 2) as averageCompletionTime,
            0 as rank
        FROM vocab_topics vt
        LEFT JOIN vocab_user_progress vup ON vup.topic_id = vt.id
        LEFT JOIN users u ON u.id = vup.user_id
        WHERE vt.is_active = 1
        GROUP BY vt.id, vt.name, vt.total_words
        HAVING usersStarted > 0
        ORDER BY completionRate DESC
    """, nativeQuery = true)
    List<Object[]> getRetentionAnalysisRaw();
    
    /**
     * Get daily active users for time-series chart (last 30 days)
     */
    @Query("""
        SELECT 
            CAST(uds.date AS LocalDate) as date,
            COUNT(DISTINCT uds.user.id) as activeUsers
        FROM UserDailyStatsEntity uds
        WHERE uds.date >= :startDate
          AND uds.isStudyDay = true
        GROUP BY uds.date
        ORDER BY uds.date
    """)
    List<Object[]> getDailyActiveUsers(@Param("startDate") LocalDate startDate);
    
    /**
     * Get user growth over time
     */
    @Query("""
        SELECT 
            CAST(u.createdAt AS LocalDate) as date,
            COUNT(u.id) as newUsers
        FROM UserEntity u
        WHERE u.createdAt >= :startDate
        GROUP BY CAST(u.createdAt AS LocalDate)
        ORDER BY date
    """)
    List<Object[]> getUserGrowth(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Get level distribution for pie chart
     */
    @Query("""
        SELECT 
            l.levelNumber,
            l.levelName,
            COUNT(u.id) as userCount
        FROM UserEntity u
        LEFT JOIN LevelEntity l ON u.totalXp BETWEEN l.minXp AND COALESCE(l.maxXp, 999999999)
        GROUP BY l.levelNumber, l.levelName
        ORDER BY l.levelNumber
    """)
    List<Object[]> getLevelDistribution();
    
    /**
     * Calculate churn rate (users inactive > 30 days)
     */
    @Query("""
        SELECT COUNT(u.id)
        FROM UserEntity u
        LEFT JOIN UserStreakEntity s ON s.user.id = u.id
        WHERE s.lastActivityDate < :thresholdDate OR s.lastActivityDate IS NULL
    """)
    Long countInactiveUsers(@Param("thresholdDate") LocalDate thresholdDate);
    
    /**
     * Get average study time across all users
     */
    @Query("""
        SELECT AVG(uds.studyTimeMinutes)
        FROM UserDailyStatsEntity uds
        WHERE uds.isStudyDay = true
          AND (:startDate IS NULL OR uds.date >= :startDate)
          AND (:endDate IS NULL OR uds.date <= :endDate)
    """)
    Double getAverageStudyTime(
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate
    );
}

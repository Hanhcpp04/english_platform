package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserGrammarProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface UserGrammarProgressRepository extends JpaRepository<UserGrammarProgressEntity, Long> {
    long countByIsCompletedTrue();
    
    // For Excel Report
    long countByTypeAndIsCompleted(UserGrammarProgressEntity.ProgressType type, Boolean isCompleted);
    long countByCompletedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Đếm số bài học unique đã hoàn thành
    @Query("SELECT COUNT(DISTINCT g.lesson.id) FROM UserGrammarProgressEntity g WHERE g.user.id = :userId AND g.isCompleted = true")
    int countDistinctLessonsByUserIdAndIsCompletedTrue(@Param("userId") Long userId);

    // Count completed lessons by user and topic
    @Query("SELECT COUNT(DISTINCT ugp.lesson.id) FROM UserGrammarProgressEntity ugp " +
           "WHERE ugp.user.id = :userId " +
           "AND ugp.topic.id = :topicId " +
           "AND ugp.isCompleted = true " +
           "AND ugp.type = 'theory'")
    Integer countCompletedLessonsByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);

    // tổng tất cả xp trong bẳng
    @Query("""
    SELECT COALESCE(SUM(l.xpReward), 0)
    FROM GrammarLessonEntity l
    WHERE l.id IN (
        SELECT DISTINCT ugp.lesson.id
        FROM UserGrammarProgressEntity ugp
        WHERE ugp.user.id = :userId
          AND ugp.isCompleted = true
    )
""")
    Integer sumXpEarnedByUserId(@Param("userId") Long userId);

    // Count completed topics (where all lessons are completed)
    @Query("SELECT COUNT(DISTINCT ugp.topic.id) FROM UserGrammarProgressEntity ugp " +
           "WHERE ugp.user.id = :userId " +
           "AND ugp.isCompleted = true " +
           "AND ugp.type = 'theory' " +
           "AND NOT EXISTS (" +
           "    SELECT 1 FROM GrammarLessonEntity gl " +
           "    WHERE gl.topic.id = ugp.topic.id " +
           "    AND gl.isActive = true " +
           "    AND NOT EXISTS (" +
           "        SELECT 1 FROM UserGrammarProgressEntity ugp2 " +
           "        WHERE ugp2.user.id = :userId " +
           "        AND ugp2.lesson.id = gl.id " +
           "        AND ugp2.isCompleted = true " +
           "        AND ugp2.type = 'theory'" +
           "    )" +
           ")")
    Integer countCompletedTopicsByUserId(@Param("userId") Long userId);

    // Check if user has any progress in a topic
    @Query("SELECT COUNT(ugp) > 0 FROM UserGrammarProgressEntity ugp " +
           "WHERE ugp.user.id = :userId AND ugp.topic.id = :topicId")
    boolean existsByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);


    @Query("SELECT ugp FROM UserGrammarProgressEntity ugp " +
           "WHERE ugp.user.id = :userId " +
           "AND ugp.topic.id = :topicId " +
           "AND ugp.type = 'theory'")
    java.util.List<UserGrammarProgressEntity> findByUserIdAndTopicIdAndTypeTheory(
            @Param("userId") Long userId,
            @Param("topicId") Long topicId);

    // Find progress by user, lesson and type
    @Query("SELECT ugp FROM UserGrammarProgressEntity ugp " +
           "WHERE ugp.user.id = :userId " +
           "AND ugp.lesson.id = :lessonId " +
           "AND ugp.type = :type")
    java.util.Optional<UserGrammarProgressEntity> findByUserIdAndLessonIdAndType(
            @Param("userId") Long userId,
            @Param("lessonId") Long lessonId,
            @Param("type") UserGrammarProgressEntity.ProgressType type);
}
package com.back_end.english_app.repository;

import com.back_end.english_app.entity.VocabUserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabUserProgressRepository extends JpaRepository<VocabUserProgress , Long> {
    int countByUserIdAndIsCompletedTrue(Long userId);
    // mà một người dùng đã học
    @Query("SELECT COUNT(DISTINCT vup.word.id) FROM VocabUserProgress vup " +
            "WHERE vup.user.id = :userId AND vup.isCompleted = true")
    Integer countWordsLearnedByUserId(@Param("userId") Long userId);
    // đếm số topic hoàn thành của user
    @Query("SELECT COUNT(DISTINCT vup.topic.id) FROM VocabUserProgress vup " +
            "WHERE vup.user.id = :userId " +
            "GROUP BY vup.topic.id " +
            "HAVING COUNT(DISTINCT vup.word.id) = " +
            "(SELECT vt.totalWords FROM VocabTopicEntity vt WHERE vt.id = vup.topic.id)")
    Integer countCompletedTopicsByUserId(@Param("userId") Long userId);
    // đếm số từ hoàn thành / 1 topic
    @Query("SELECT vup.topic.id, COUNT(DISTINCT vup.word.id) " +
            "FROM VocabUserProgress vup " +
            "WHERE vup.user.id = :userId AND vup.isCompleted = true " +
            "GROUP BY vup.topic.id")
    List<Object[]> countWordsLearnedPerTopic(@Param("userId") Long userId);
    // tinh tong so xp nguoi dung dat duoc
    @Query("SELECT COALESCE(SUM(vup.word.xpReward), 0) FROM VocabUserProgress vup " +
            "WHERE vup.user.id = :userId AND vup.isCompleted = true")
    Integer sumXpEarnedByUserId(@Param("userId") Long userId);
    // lay tien trinh topic
    @Query("SELECT vup.topic.id, COUNT(DISTINCT vup.word.id), COALESCE(SUM(vup.word.xpReward), 0) " +
            "FROM VocabUserProgress vup " +
            "WHERE vup.user.id = :userId AND vup.isCompleted = true " +
            "GROUP BY vup.topic.id")
    List<Object[]> getTopicProgressByUserId(@Param("userId") Long userId);

    // Tìm progress của user với từ cụ thể trong topic
    Optional<VocabUserProgress> findByUserIdAndWordIdAndTopicId(Long userId, Long wordId, Long topicId);

    // Lấy tất cả progress của user trong một topic
    List<VocabUserProgress> findByUserIdAndTopicId(Long userId, Long topicId);

    // Lấy progress theo user, topic và type (flashcard hoặc exercise)
    @Query("SELECT vup FROM VocabUserProgress vup " +
            "WHERE vup.user.id = :userId AND vup.topic.id = :topicId AND vup.type = :type")
    List<VocabUserProgress> findByUserIdAndTopicIdAndType(
            @Param("userId") Long userId,
            @Param("topicId") Long topicId,
            @Param("type") VocabUserProgress.ProgressType type);

    // Lấy progress theo user, question và type
    @Query("SELECT vup FROM VocabUserProgress vup " +
            "WHERE vup.user.id = :userId AND vup.question.id = :questionId AND vup.type = :type")
    Optional<VocabUserProgress> findByUserIdAndQuestionIdAndType(
            @Param("userId") Long userId,
            @Param("questionId") Long questionId,
            @Param("type") VocabUserProgress.ProgressType type);
}

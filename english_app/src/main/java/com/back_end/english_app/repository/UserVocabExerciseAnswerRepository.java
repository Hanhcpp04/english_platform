package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserVocabExerciseAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVocabExerciseAnswerRepository extends JpaRepository<UserVocabExerciseAnswerEntity, Long> {

    /**
     * Tìm tất cả câu trả lời của user cho một question cụ thể
     */
    List<UserVocabExerciseAnswerEntity> findByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * Tìm tất cả câu trả lời của user theo topic và type
     * Sắp xếp theo thời gian mới nhất lên đầu
     */
    @Query("SELECT a FROM UserVocabExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.topic.id = :topicId " +
           "AND a.type.id = :typeId " +
           "ORDER BY a.attemptedAt DESC")
    List<UserVocabExerciseAnswerEntity> findAnswersByUserTopicAndType(
            @Param("userId") Long userId,
            @Param("topicId") Long topicId,
            @Param("typeId") Long typeId);

    /**
     * Xóa tất cả câu trả lời của user trong một topic và type cụ thể (để reset)
     */
    @Modifying
    @Query("DELETE FROM UserVocabExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.topic.id = :topicId " +
           "AND a.type.id = :typeId")
    void deleteByUserIdAndTopicIdAndTypeId(
            @Param("userId") Long userId,
            @Param("topicId") Long topicId,
            @Param("typeId") Long typeId);

    /**
     * Đếm tổng số câu trả lời của user theo topic và type
     */
    @Query("SELECT COUNT(a) FROM UserVocabExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.topic.id = :topicId " +
           "AND a.type.id = :typeId")
    Long countTotalAnswers(@Param("userId") Long userId,
                          @Param("topicId") Long topicId,
                          @Param("typeId") Long typeId);

    /**
     * Đếm số câu trả lời đúng của user theo topic và type
     */
    @Query("SELECT COUNT(a) FROM UserVocabExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.topic.id = :topicId " +
           "AND a.type.id = :typeId " +
           "AND a.isCorrect = true")
    Long countCorrectAnswers(@Param("userId") Long userId,
                            @Param("topicId") Long topicId,
                            @Param("typeId") Long typeId);

    /**
     * Đếm số câu trả lời sai của user theo topic và type
     */
    @Query("SELECT COUNT(a) FROM UserVocabExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.topic.id = :topicId " +
           "AND a.type.id = :typeId " +
           "AND a.isCorrect = false")
    Long countIncorrectAnswers(@Param("userId") Long userId,
                              @Param("topicId") Long topicId,
                              @Param("typeId") Long typeId);

    /**
     * Đếm số câu hỏi unique (distinct) đã trả lời
     */
    @Query("SELECT COUNT(DISTINCT a.question.id) FROM UserVocabExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.topic.id = :topicId " +
           "AND a.type.id = :typeId")
    Long countDistinctAnsweredQuestions(@Param("userId") Long userId,
                                       @Param("topicId") Long topicId,
                                       @Param("typeId") Long typeId);

    /**
     * Tính tổng XP đã kiếm được từ bài tập
     */
    @Query("SELECT COALESCE(SUM(a.xpEarned), 0) FROM UserVocabExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.topic.id = :topicId " +
           "AND a.type.id = :typeId")
    Long sumXpEarned(@Param("userId") Long userId,
                    @Param("topicId") Long topicId,
                    @Param("typeId") Long typeId);
}


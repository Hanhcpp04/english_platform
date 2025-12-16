package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserGrammarExerciseAnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
@Repository
public interface UserGrammarExerciseAnswerRepository extends JpaRepository<UserGrammarExerciseAnswerEntity, Long> {

    // Tìm tất cả câu trả lời của user cho một question cụ thể
    List<UserGrammarExerciseAnswerEntity> findByUserIdAndQuestionId(Long userId, Long questionId);

    // Tìm tất cả câu trả lời của user theo lesson và type
    @Query("SELECT a FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId " +
           "AND a.type.id = :typeId " +
           "ORDER BY a.attemptedAt DESC")
    List<UserGrammarExerciseAnswerEntity> findAnswersByUserLessonAndType(
            @Param("userId") Long userId,
            @Param("lessonId") Long lessonId,
            @Param("typeId") Long typeId);

    // Xóa tất cả câu trả lời của user trong một lesson cụ thể
    @Modifying
    @Query("DELETE FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId")
    void deleteByUserIdAndLessonId(@Param("userId") Long userId, @Param("lessonId") Long lessonId);

    // Xóa tất cả câu trả lời của user trong một lesson và type cụ thể (để reset từng loại bài tập)
    @Modifying
    @Query("DELETE FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId " +
           "AND a.type.id = :typeId")
    void deleteByUserIdAndLessonIdAndTypeId(
            @Param("userId") Long userId,
            @Param("lessonId") Long lessonId,
            @Param("typeId") Long typeId);

    // Đếm số câu hỏi đã trả lời trong một lesson
    @Query("SELECT COUNT(DISTINCT a.question.id) FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId")
    Integer countAnsweredQuestionsByUserAndLesson(@Param("userId") Long userId, @Param("lessonId") Long lessonId);

    // Đếm tổng số câu trả lời của user theo lesson và type
    @Query("SELECT COUNT(a) FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId " +
           "AND a.type.id = :typeId")
    Long countTotalAnswers(@Param("userId") Long userId,
                          @Param("lessonId") Long lessonId,
                          @Param("typeId") Long typeId);

    // Đếm số câu trả lời đúng của user theo lesson và type
    @Query("SELECT COUNT(a) FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId " +
           "AND a.type.id = :typeId " +
           "AND a.isCorrect = true")
    Long countCorrectAnswers(@Param("userId") Long userId,
                            @Param("lessonId") Long lessonId,
                            @Param("typeId") Long typeId);

    // Đếm số câu trả lời sai của user theo lesson và type
    @Query("SELECT COUNT(a) FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId " +
           "AND a.type.id = :typeId " +
           "AND a.isCorrect = false")
    Long countIncorrectAnswers(@Param("userId") Long userId,
                              @Param("lessonId") Long lessonId,
                              @Param("typeId") Long typeId);

    // Đếm số câu hỏi unique (distinct) đã trả lời
    @Query("SELECT COUNT(DISTINCT a.question.id) FROM UserGrammarExerciseAnswerEntity a " +
           "WHERE a.user.id = :userId " +
           "AND a.question.lesson.id = :lessonId " +
           "AND a.type.id = :typeId")
    Long countDistinctAnsweredQuestions(@Param("userId") Long userId,
                                       @Param("lessonId") Long lessonId,
                                       @Param("typeId") Long typeId);
}


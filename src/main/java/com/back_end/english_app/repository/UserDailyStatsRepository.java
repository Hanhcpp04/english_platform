package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserDailyStatsEntity;
import com.back_end.english_app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDailyStatsRepository extends JpaRepository<UserDailyStatsEntity, Long> {
    
    Optional<UserDailyStatsEntity> findByUserAndDate(UserEntity user, LocalDate date);
    
    boolean existsByUserIdAndDateBetweenAndIsStudyDayTrue(Long userId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT SUM(uds.vocabLearned) FROM UserDailyStatsEntity uds WHERE uds.date BETWEEN :startDate AND :endDate")
    Long sumVocabLearnedBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(uds.grammarCompleted) FROM UserDailyStatsEntity uds WHERE uds.date BETWEEN :startDate AND :endDate")
    Long sumGrammarCompletedBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(uds.writingSubmitted) FROM UserDailyStatsEntity uds WHERE uds.date BETWEEN :startDate AND :endDate")
    Long sumWritingSubmittedBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT SUM(uds.exercisesDone) FROM UserDailyStatsEntity uds WHERE uds.date BETWEEN :startDate AND :endDate")
    Long sumExercisesDoneBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}

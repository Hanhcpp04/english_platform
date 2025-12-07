package com.englishplatform.repository;

import com.englishplatform.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findByUserIdOrderByCompletedAtDesc(Long userId);
    List<QuizResult> findByQuizIdOrderByCompletedAtDesc(Long quizId);
    
    @Query("SELECT AVG(qr.percentage) FROM QuizResult qr WHERE qr.user.id = :userId")
    Double findAverageScoreByUserId(Long userId);
    
    @Query("SELECT COUNT(qr) FROM QuizResult qr WHERE qr.user.id = :userId")
    Long countByUserId(Long userId);
}
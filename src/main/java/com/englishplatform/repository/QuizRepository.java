package com.englishplatform.repository;

import com.englishplatform.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByDifficulty(Quiz.DifficultyLevel difficulty);
    List<Quiz> findByTitleContainingIgnoreCase(String title);
}
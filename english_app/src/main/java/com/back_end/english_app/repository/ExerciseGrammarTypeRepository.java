package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ExerciseGrammarTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseGrammarTypeRepository extends JpaRepository<ExerciseGrammarTypeEntity, Long> {
    List<ExerciseGrammarTypeEntity> findByTopicIdAndIsActiveTrue(Long topicId);
}


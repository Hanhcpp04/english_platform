package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ExerciseGrammarTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseGrammarTypeRepository extends JpaRepository<ExerciseGrammarTypeEntity, Long> {
    List<ExerciseGrammarTypeEntity> findByTopicIdAndIsActiveTrue(Long topicId);
    
    Page<ExerciseGrammarTypeEntity> findByTopic_Id(Long topicId, Pageable pageable);
}


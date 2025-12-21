package com.back_end.english_app.repository;

import com.back_end.english_app.entity.WritingGradingCriteriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WritingGradingCriteriaRepository extends JpaRepository<WritingGradingCriteriaEntity, Integer> {
    
    Optional<WritingGradingCriteriaEntity> findByTaskId(Integer taskId);
    
    void deleteByTaskId(Integer taskId);
}

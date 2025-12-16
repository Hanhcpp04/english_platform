package com.back_end.english_app.repository;

import com.back_end.english_app.entity.WritingPromptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WritingPromptRepository extends JpaRepository<WritingPromptEntity, Integer> {

    @Query("SELECT p FROM WritingPromptEntity p WHERE p.writingTask.id = :taskId AND p.user.id = :userId AND p.isActive = true ORDER BY p.submittedAt DESC")
    List<WritingPromptEntity> findAllByTaskIdAndUserId(@Param("taskId") Integer taskId, @Param("userId") Long userId);

    // Count completed prompts for a user
    long countByUserIdAndIsCompletedTrue(Long userId);
}
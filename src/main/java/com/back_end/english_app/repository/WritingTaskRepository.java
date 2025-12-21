package com.back_end.english_app.repository;

import com.back_end.english_app.entity.WrittingTaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WritingTaskRepository extends JpaRepository<WrittingTaskEntity, Integer> {

    @Query("SELECT t FROM WrittingTaskEntity t WHERE t.writingTopic.id = :topicId AND t.isActive = true ORDER BY t.id ASC")
    List<WrittingTaskEntity> findAllByTopicId(@Param("topicId") Integer topicId);
    
    Page<WrittingTaskEntity> findByWritingTopicId(Integer topicId, Pageable pageable);
    
    @Query("SELECT COUNT(t) FROM WrittingTaskEntity t WHERE t.writingTopic.id = :topicId")
    long countByTopicId(@Param("topicId") Integer topicId);
}


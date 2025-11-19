package com.back_end.english_app.repository;

import com.back_end.english_app.entity.WritingTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WritingTopicRepository extends JpaRepository<WritingTopicEntity, Integer> {

    @Query("SELECT t FROM WritingTopicEntity t WHERE t.isActive = true ORDER BY t.id ASC")
    List<WritingTopicEntity> findAllActiveTopics();
}


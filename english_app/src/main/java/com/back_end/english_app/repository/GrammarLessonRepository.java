package com.back_end.english_app.repository;

import com.back_end.english_app.entity.GrammarLessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarLessonRepository extends JpaRepository<GrammarLessonEntity, Long> {

    // Count total lessons by topic
    @Query("SELECT COUNT(gl) FROM GrammarLessonEntity gl " +
           "WHERE gl.topic.id = :topicId AND gl.isActive = true")
    Integer countByTopicId(@Param("topicId") Long topicId);

    // Get all lessons by topic
    List<GrammarLessonEntity> findByTopicIdAndIsActiveTrue(Long topicId);
}


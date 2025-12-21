package com.back_end.english_app.repository;

import com.back_end.english_app.entity.GrammarTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarTopicRepository extends JpaRepository<GrammarTopicEntity, Long> {

    // Get all active topics ordered by creation date
    List<GrammarTopicEntity> findByIsActiveTrueOrderByCreatedAtAsc();

    // Count completed topics by user
    @Query("SELECT COUNT(DISTINCT ugp.topic.id) FROM UserGrammarProgressEntity ugp " +
           "WHERE ugp.user.id = :userId " +
           "AND ugp.isCompleted = true " +
           "AND ugp.type = 'theory'")
    Integer countCompletedTopicsByUserId(@Param("userId") Long userId);
}


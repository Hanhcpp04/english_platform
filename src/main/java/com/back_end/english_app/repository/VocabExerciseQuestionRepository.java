package com.back_end.english_app.repository;

import com.back_end.english_app.entity.VocabExerciseQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabExerciseQuestionRepository extends JpaRepository<VocabExerciseQuestionEntity, Long> {

    /**
     * Đếm số câu hỏi theo type và topic
     */
    @Query("SELECT COUNT(q) FROM VocabExerciseQuestionEntity q " +
           "WHERE q.type.id = :typeId " +
           "AND q.isActive = true")
    Integer countByTypeId(@Param("typeId") Long typeId);
}


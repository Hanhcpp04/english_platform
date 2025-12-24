package com.back_end.english_app.repository;

import com.back_end.english_app.entity.GrammarQuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarQuestionRepository extends JpaRepository<GrammarQuestionEntity, Long> {

    // Simplified: No need for topic_id since lesson already belongs to a specific topic
    List<GrammarQuestionEntity> findByLessonIdAndTypeIdAndIsActiveTrue(Long lessonId, Long typeId);


    @Query("SELECT COUNT(q) FROM GrammarQuestionEntity q " +
           "WHERE q.lesson.id = :lessonId " +
           "AND q.type.id = :typeId " +
           "AND q.isActive = true")
    Integer countByLessonIdAndTypeId(
            @Param("lessonId") Long lessonId,
            @Param("typeId") Long typeId);
    
    Integer countByType_Id(Long typeId);
    
    Page<GrammarQuestionEntity> findByType_Id(Long typeId, Pageable pageable);
    
    Page<GrammarQuestionEntity> findByLesson_Id(Long lessonId, Pageable pageable);
    
    long countByIsActiveTrue();
}


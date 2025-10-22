package com.back_end.english_app.repository;

import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.entity.VocabWordEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabTopicRepository extends JpaRepository<VocabTopicEntity ,Long> {
    List<VocabTopicEntity> findAllByIsActiveTrue();

    //cập nhật lại từ mới thuộc topic bị xóa
    @Modifying
    @Transactional
    @Query("UPDATE VocabWordEntity w SET w.isActive = false WHERE w.topic.id = :topicId")
    void deactivateByTopicId(@Param("topicId") Long topicId);

    @Query("SELECT t FROM VocabTopicEntity t WHERE (LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(t.englishName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND t.isActive = true")
    List<VocabTopicEntity> findByName(@Param("keyword") String nameKeyword, @Param("keyword") String englishNameKeyword);
}

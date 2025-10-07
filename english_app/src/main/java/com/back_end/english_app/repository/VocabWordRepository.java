package com.back_end.english_app.repository;

import com.back_end.english_app.entity.VocabWordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabWordRepository extends JpaRepository<VocabWordEntity, Long> {
    List<VocabWordEntity> findByTopicIdAndIsActiveTrue(Long topicId);
}


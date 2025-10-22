package com.back_end.english_app.repository;

import com.back_end.english_app.entity.VocabWordEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VocabWordRepository extends JpaRepository<VocabWordEntity, Long> {
    List<VocabWordEntity> findByTopicIdAndIsActiveTrue(Long topicId);
    Page<VocabWordEntity> findAllByTopic_IsActiveTrue(Pageable pageable);

}


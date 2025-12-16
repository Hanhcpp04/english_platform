package com.back_end.english_app.repository;

import com.back_end.english_app.entity.VocabTopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VocabTopicRepository extends JpaRepository<VocabTopicEntity ,Long> {
    List<VocabTopicEntity> findAllByIsActiveTrue();
    Optional<VocabTopicEntity> findByEnglishName(String name);
}

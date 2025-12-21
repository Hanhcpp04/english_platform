package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserVocabProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVocabProgressRepository extends JpaRepository<UserVocabProgressEntity, Long> {
    long countByIsCompletedTrue();
    long countByTopicIdAndIsCompletedTrue(Long topicId);
    long countByTopicIdAndIsCompletedFalse(Long topicId);
}

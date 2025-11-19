package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ForumPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPostEntity, Long> {
    int countByUserIdAndIsActiveTrue(Long userId);
}

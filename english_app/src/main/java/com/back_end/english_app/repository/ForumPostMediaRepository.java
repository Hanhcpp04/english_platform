package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ForumPostMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumPostMediaRepository extends JpaRepository<ForumPostMediaEntity, Long> {

    List<ForumPostMediaEntity> findByPostIdAndIsActiveTrue(Long postId);

    List<ForumPostMediaEntity> findByIdInAndIsActiveTrue(List<Long> ids);
}


package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ForumPostViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForumPostViewRepository extends JpaRepository<ForumPostViewEntity, Long> {

    boolean existsByPostIdAndUserIdAndViewedAtAfter(Long postId, Long userId, java.time.LocalDateTime after);

    boolean existsByPostIdAndIpAddressAndViewedAtAfter(Long postId, String ipAddress, java.time.LocalDateTime after);

    Integer countByPostId(Long postId);
}


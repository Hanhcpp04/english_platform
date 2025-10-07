package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserBadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity, Long> {
    List<UserBadgeEntity> findByUserId(Long userId);
}

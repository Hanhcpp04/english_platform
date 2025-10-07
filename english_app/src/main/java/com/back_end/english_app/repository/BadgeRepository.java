package com.back_end.english_app.repository;

import com.back_end.english_app.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<BadgeEntity, Long> {
}

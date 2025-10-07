package com.back_end.english_app.repository;

import com.back_end.english_app.entity.BadgeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BadgeRepository extends JpaRepository<BadgeEntity ,Long> {
    List<BadgeEntity> findAllByIsActiveTrueOrderByCreatedAtDesc();
    Long countByIsActiveTrue();

}

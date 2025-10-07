package com.back_end.english_app.repository;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.UserStreakEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStreakRepository extends JpaRepository<UserStreakEntity, Long> {
    Optional<UserStreakEntity> findByUserId(Long userId);
}
package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserStreakEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreakEntity, Long> {
    Optional<UserStreakEntity> findByUserId(Long userId);

}


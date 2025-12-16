package com.back_end.english_app.repository;

import com.back_end.english_app.entity.RefreshTokenEntity;
import com.back_end.english_app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenAndIsActiveTrue(String token);
    void deleteByUser(UserEntity user);
    void deleteByToken(String token);
}


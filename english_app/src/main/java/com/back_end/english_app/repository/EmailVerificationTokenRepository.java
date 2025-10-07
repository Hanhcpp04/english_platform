package com.back_end.english_app.repository;

import com.back_end.english_app.entity.EmailVerificationTokenEntity;
import com.back_end.english_app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationTokenEntity, Long> {
    
    Optional<EmailVerificationTokenEntity> findByToken(String token);
    
    Optional<EmailVerificationTokenEntity> findByUser(UserEntity user);
    
    @Modifying
    @Query("UPDATE EmailVerificationTokenEntity e SET e.confirmedAt = ?2 WHERE e.token = ?1")
    int updateConfirmedAt(String token, LocalDateTime confirmedAt);
    
    @Modifying
    @Query("DELETE FROM EmailVerificationTokenEntity e WHERE e.expiresAt < ?1")
    void deleteExpiredTokens(LocalDateTime now);
}

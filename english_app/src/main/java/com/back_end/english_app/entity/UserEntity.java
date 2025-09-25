package com.back_end.english_app.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true, length = 100)
    String username;

    @Column(nullable = false, unique = true)
    String email;

    @Column(name = "password_hash")
    String passwordHash;

    @Column(nullable = false)
    String fullname;

    @Column(length = 500)
    String avatar;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('ADMIN', 'USER') DEFAULT 'USER'")
    Role role = Role.USER;

    @Column(name = "google_id", unique = true)
    String googleId;

    @Column(name = "facebook_id", unique = true)
    String facebookId;

    @Column(name = "total_xp", columnDefinition = "INT DEFAULT 0")
    Integer totalXp = 0;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    Boolean isActive = true;

    @Column(name = "is_email_verified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean isEmailVerified = false;

    @Column(name = "email_verification_token")
    String emailVerificationToken;

    @Column(name = "email_verification_token_expires_at")
    LocalDateTime emailVerificationTokenExpiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}

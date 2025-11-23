package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity , Long> {
    Optional<UserEntity> findByIdAndIsActiveTrue(Long id);
    Optional<UserEntity> findByEmailAndIsActiveTrue(String email);

    // Tìm user theo email mà không cần check isActive (dùng để xử lý trường hợp duplicate hoặc inactive)
    Optional<UserEntity> findByEmail(String email);
    boolean existsByUsername(String username);

    List<UserEntity> findByIsActiveTrue();
}


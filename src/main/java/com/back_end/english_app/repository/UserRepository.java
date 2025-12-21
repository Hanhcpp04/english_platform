package com.back_end.english_app.repository;

import com.back_end.english_app.entity.Role;
import com.back_end.english_app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
    
    // For Excel Report
    long countByIsActive(Boolean isActive);
    long countByRole(Role role);
    
    @Query("SELECT SUM(u.totalXp) FROM UserEntity u")
    Long sumTotalXp();
}



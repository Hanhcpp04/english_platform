package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ForumPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ForumPostRepository extends JpaRepository<ForumPostEntity, Long> {
    int countByUserIdAndIsActiveTrue(Long userId);

    // Find all active posts with pagination
    Page<ForumPostEntity> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    // Search posts by title or content
    @Query("SELECT p FROM ForumPostEntity p WHERE p.isActive = true " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<ForumPostEntity> searchPosts(@Param("search") String search, Pageable pageable);

    // Filter posts by date range
    @Query("SELECT p FROM ForumPostEntity p WHERE p.isActive = true " +
           "AND p.createdAt >= :startDate " +
           "ORDER BY p.createdAt DESC")
    Page<ForumPostEntity> findByCreatedAtAfter(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    // Search with date filter
    @Query("SELECT p FROM ForumPostEntity p WHERE p.isActive = true " +
           "AND p.createdAt >= :startDate " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<ForumPostEntity> searchPostsWithDateFilter(
            @Param("search") String search,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );

    // Find posts by userId
    Page<ForumPostEntity> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // Find posts by userId with date filter
    @Query("SELECT p FROM ForumPostEntity p WHERE p.isActive = true " +
           "AND p.user.id = :userId " +
           "AND p.createdAt >= :startDate " +
           "ORDER BY p.createdAt DESC")
    Page<ForumPostEntity> findByUserIdAndCreatedAtAfter(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );

    // Search posts by userId
    @Query("SELECT p FROM ForumPostEntity p WHERE p.isActive = true " +
           "AND p.user.id = :userId " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<ForumPostEntity> searchPostsByUserId(
            @Param("userId") Long userId,
            @Param("search") String search,
            Pageable pageable
    );

    // Search posts by userId with date filter
    @Query("SELECT p FROM ForumPostEntity p WHERE p.isActive = true " +
           "AND p.user.id = :userId " +
           "AND p.createdAt >= :startDate " +
           "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY p.createdAt DESC")
    Page<ForumPostEntity> searchPostsByUserIdWithDateFilter(
            @Param("userId") Long userId,
            @Param("search") String search,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );
}

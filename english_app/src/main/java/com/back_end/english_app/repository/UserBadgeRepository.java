package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserBadgeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity , Long> {

    @Query("SELECT ub FROM UserBadgeEntity ub " +
            "WHERE ub.user.id = :userId " +
            "ORDER BY ub.earnedAt DESC")
    List<UserBadgeEntity> findRecentBadgesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ub FROM UserBadgeEntity ub " +
            "WHERE ub.user.id = :userId")
    List<UserBadgeEntity> findAllByUserId(@Param("userId") Long userId);

    Long countByUserId(Long userId);

    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);
}

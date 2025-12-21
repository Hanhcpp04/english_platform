package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserBadgeEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadgeEntity , Long> {

    @Query("SELECT ub FROM UserBadgeEntity ub " +
            "WHERE ub.user.id = :userId " +
            "ORDER BY ub.earnedAt DESC")
    List<UserBadgeEntity> findRecentBadgesByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT ub FROM UserBadgeEntity ub " +
            "WHERE ub.user.id = :userId")
    List<UserBadgeEntity> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(ub) FROM UserBadgeEntity ub WHERE ub.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(ub) > 0 THEN true ELSE false END FROM UserBadgeEntity ub " +
            "WHERE ub.user.id = :userId AND ub.badge.id = :badgeId")
    boolean existsByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    @Query("SELECT ub FROM UserBadgeEntity ub " +
            "WHERE ub.user.id = :userId AND ub.badge.id = :badgeId")
    Optional<UserBadgeEntity> findByUserIdAndBadgeId(@Param("userId") Long userId, @Param("badgeId") Long badgeId);

    @Query("SELECT ub FROM UserBadgeEntity ub " +
            "WHERE ub.user.id = :userId AND ub.badge.id IN :badgeIds")
    List<UserBadgeEntity> findByUserIdAndBadgeIdIn(@Param("userId") Long userId, @Param("badgeIds") List<Long> badgeIds);
}

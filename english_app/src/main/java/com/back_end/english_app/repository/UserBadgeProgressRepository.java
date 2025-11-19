package com.back_end.english_app.repository;

import com.back_end.english_app.entity.UserBadgeProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho UserBadgeProgress
 */
@Repository
public interface UserBadgeProgressRepository extends JpaRepository<UserBadgeProgressEntity, Long> {


    Optional<UserBadgeProgressEntity> findByUserIdAndBadgeId(Long userId, Long badgeId);


    List<UserBadgeProgressEntity> findAllByUserId(Long userId);


    List<UserBadgeProgressEntity> findByUserIdOrderByProgressPercentageDesc(Long userId);


    boolean existsByUserIdAndBadgeId(Long userId, Long badgeId);


    void deleteByUserId(Long userId);

    List<UserBadgeProgressEntity> findByUserIdAndProgressPercentageGreaterThanEqualOrderByProgressPercentageDesc(
        Long userId, Double minPercentage);
}


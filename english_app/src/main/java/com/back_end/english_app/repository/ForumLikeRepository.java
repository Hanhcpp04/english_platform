package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ForumLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForumLikeRepository extends JpaRepository<ForumLikeEntity, Long> {

    Optional<ForumLikeEntity> findByUserIdAndTargetTypeAndTargetId(
            Long userId,
            ForumLikeEntity.TargetType targetType,
            Long targetId
    );

    boolean existsByUserIdAndTargetTypeAndTargetId(
            Long userId,
            ForumLikeEntity.TargetType targetType,
            Long targetId
    );

    void deleteByUserIdAndTargetTypeAndTargetId(
            Long userId,
            ForumLikeEntity.TargetType targetType,
            Long targetId
    );
}


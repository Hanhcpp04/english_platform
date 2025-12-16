package com.back_end.english_app.repository;

import com.back_end.english_app.entity.ForumCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumCommentEntity, Long> {

    @Query("SELECT c FROM ForumCommentEntity c WHERE c.post.id = :postId AND c.parent IS NULL AND c.isActive = true ORDER BY c.createdAt DESC")
    Page<ForumCommentEntity> findRootCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("SELECT c FROM ForumCommentEntity c WHERE c.parent.id = :parentId AND c.isActive = true ORDER BY c.createdAt ASC")
    List<ForumCommentEntity> findRepliesByParentId(@Param("parentId") Long parentId);

    int countByPostIdAndIsActiveTrue(Long postId);

    int countByUserIdAndIsActiveTrue(Long userId);
}


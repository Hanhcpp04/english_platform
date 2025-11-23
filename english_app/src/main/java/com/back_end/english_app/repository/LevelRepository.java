package com.back_end.english_app.repository;

import com.back_end.english_app.entity.LevelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LevelRepository extends JpaRepository<LevelEntity , Integer> {

    @Query("SELECT l FROM LevelEntity l WHERE l.minXp <= :totalXp AND (l.maxXp >= :totalXp OR l.maxXp IS NULL) ORDER BY l.levelNumber DESC")
    Optional<LevelEntity> findLevelByTotalXp(@Param("totalXp") Integer totalXp);

    Optional<LevelEntity> findByLevelNumber(Integer levelNumber);

    //lấy ra level cao nhất
    Optional<LevelEntity> findTopByOrderByLevelNumberDesc();

}

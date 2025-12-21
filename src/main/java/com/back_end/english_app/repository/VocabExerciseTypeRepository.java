package com.back_end.english_app.repository;

import com.back_end.english_app.entity.VocabExerciseTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabExerciseTypeRepository extends JpaRepository<VocabExerciseTypeEntity, Long> {
}


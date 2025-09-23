package com.englishplatform.repository;

import com.englishplatform.model.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyRepository extends JpaRepository<Vocabulary, Long> {
    Optional<Vocabulary> findByWord(String word);
    List<Vocabulary> findByDifficulty(Vocabulary.DifficultyLevel difficulty);
    List<Vocabulary> findByWordType(Vocabulary.WordType wordType);
    
    @Query("SELECT v FROM Vocabulary v WHERE LOWER(v.word) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(v.definition) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Vocabulary> findByWordOrDefinitionContainingIgnoreCase(String search);
    
    @Query(value = "SELECT * FROM vocabulary ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Vocabulary> findRandomWords(int limit);
}
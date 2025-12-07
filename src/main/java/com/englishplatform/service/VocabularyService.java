package com.englishplatform.service;

import com.englishplatform.model.Vocabulary;
import com.englishplatform.repository.VocabularyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VocabularyService {

    @Autowired
    private VocabularyRepository vocabularyRepository;

    public List<Vocabulary> findAllVocabulary() {
        return vocabularyRepository.findAll();
    }

    public Optional<Vocabulary> findById(Long id) {
        return vocabularyRepository.findById(id);
    }

    public Optional<Vocabulary> findByWord(String word) {
        return vocabularyRepository.findByWord(word);
    }

    public List<Vocabulary> findByDifficulty(Vocabulary.DifficultyLevel difficulty) {
        return vocabularyRepository.findByDifficulty(difficulty);
    }

    public List<Vocabulary> findByWordType(Vocabulary.WordType wordType) {
        return vocabularyRepository.findByWordType(wordType);
    }

    public List<Vocabulary> searchVocabulary(String search) {
        return vocabularyRepository.findByWordOrDefinitionContainingIgnoreCase(search);
    }

    public List<Vocabulary> getRandomWords(int limit) {
        return vocabularyRepository.findRandomWords(limit);
    }

    public Vocabulary saveVocabulary(Vocabulary vocabulary) {
        return vocabularyRepository.save(vocabulary);
    }

    public Vocabulary updateVocabulary(Vocabulary vocabulary) {
        return vocabularyRepository.save(vocabulary);
    }

    public void deleteVocabulary(Long id) {
        vocabularyRepository.deleteById(id);
    }

    public Vocabulary createVocabulary(String word, String definition, String pronunciation, 
                                     String example, Vocabulary.DifficultyLevel difficulty, 
                                     Vocabulary.WordType wordType) {
        Vocabulary vocabulary = new Vocabulary(word, definition);
        vocabulary.setPronunciation(pronunciation);
        vocabulary.setExample(example);
        vocabulary.setDifficulty(difficulty);
        vocabulary.setWordType(wordType);
        
        return saveVocabulary(vocabulary);
    }

    public long getVocabularyCount() {
        return vocabularyRepository.count();
    }

    public boolean existsByWord(String word) {
        return vocabularyRepository.findByWord(word).isPresent();
    }
}
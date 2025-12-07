package com.englishplatform.service;

import com.englishplatform.model.UserProgress;
import com.englishplatform.repository.UserProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserProgressService {

    @Autowired
    private UserProgressRepository userProgressRepository;

    public List<UserProgress> findAllProgress() {
        return userProgressRepository.findAll();
    }

    public Optional<UserProgress> findByUserId(Long userId) {
        return userProgressRepository.findByUserId(userId);
    }

    public UserProgress saveProgress(UserProgress progress) {
        return userProgressRepository.save(progress);
    }

    public void deleteProgress(Long id) {
        userProgressRepository.deleteById(id);
    }

    public UserProgress updateProgressAfterQuiz(Long userId, Integer score, Integer totalQuestions) {
        Optional<UserProgress> progressOpt = findByUserId(userId);
        if (progressOpt.isEmpty()) {
            throw new RuntimeException("User progress not found!");
        }

        UserProgress progress = progressOpt.get();
        progress.addQuizResult(score, totalQuestions);
        return saveProgress(progress);
    }

    public UserProgress updateWordsLearned(Long userId, int wordsCount) {
        Optional<UserProgress> progressOpt = findByUserId(userId);
        if (progressOpt.isEmpty()) {
            throw new RuntimeException("User progress not found!");
        }

        UserProgress progress = progressOpt.get();
        progress.addWordsLearned(wordsCount);
        return saveProgress(progress);
    }

    public UserProgress getOrCreateUserProgress(Long userId) {
        return findByUserId(userId).orElseThrow(() -> 
            new RuntimeException("User progress not found for user ID: " + userId));
    }
}
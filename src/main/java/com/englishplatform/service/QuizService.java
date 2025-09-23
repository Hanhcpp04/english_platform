package com.englishplatform.service;

import com.englishplatform.model.Quiz;
import com.englishplatform.model.QuizQuestion;
import com.englishplatform.model.QuizResult;
import com.englishplatform.model.User;
import com.englishplatform.repository.QuizRepository;
import com.englishplatform.repository.QuizQuestionRepository;
import com.englishplatform.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    @Autowired
    private UserProgressService userProgressService;

    // Quiz Management
    public List<Quiz> findAllQuizzes() {
        return quizRepository.findAll();
    }

    public Optional<Quiz> findQuizById(Long id) {
        return quizRepository.findById(id);
    }

    public List<Quiz> findQuizzesByDifficulty(Quiz.DifficultyLevel difficulty) {
        return quizRepository.findByDifficulty(difficulty);
    }

    public List<Quiz> searchQuizzes(String title) {
        return quizRepository.findByTitleContainingIgnoreCase(title);
    }

    public Quiz saveQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    // Quiz Question Management
    public List<QuizQuestion> findQuestionsByQuizId(Long quizId) {
        return quizQuestionRepository.findByQuizId(quizId);
    }

    public QuizQuestion saveQuizQuestion(QuizQuestion question) {
        return quizQuestionRepository.save(question);
    }

    public void deleteQuizQuestion(Long id) {
        quizQuestionRepository.deleteById(id);
    }

    // Quiz Results Management
    public List<QuizResult> findResultsByUserId(Long userId) {
        return quizResultRepository.findByUserIdOrderByCompletedAtDesc(userId);
    }

    public List<QuizResult> findResultsByQuizId(Long quizId) {
        return quizResultRepository.findByQuizIdOrderByCompletedAtDesc(quizId);
    }

    public QuizResult saveQuizResult(QuizResult result) {
        QuizResult savedResult = quizResultRepository.save(result);
        
        // Update user progress
        userProgressService.updateProgressAfterQuiz(result.getUser().getId(), 
                                                   result.getScore(), 
                                                   result.getTotalQuestions());
        return savedResult;
    }

    public Double getAverageScoreByUserId(Long userId) {
        return quizResultRepository.findAverageScoreByUserId(userId);
    }

    public Long getQuizCountByUserId(Long userId) {
        return quizResultRepository.countByUserId(userId);
    }

    // Quiz Submission and Scoring
    public QuizResult submitQuiz(User user, Long quizId, List<String> answers) {
        Optional<Quiz> quizOpt = findQuizById(quizId);
        if (quizOpt.isEmpty()) {
            throw new RuntimeException("Quiz not found!");
        }

        Quiz quiz = quizOpt.get();
        List<QuizQuestion> questions = findQuestionsByQuizId(quizId);
        
        if (answers.size() != questions.size()) {
            throw new RuntimeException("Number of answers does not match number of questions!");
        }

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion question = questions.get(i);
            String userAnswer = answers.get(i);
            if (question.getCorrectAnswer().equalsIgnoreCase(userAnswer)) {
                score++;
            }
        }

        QuizResult result = new QuizResult(user, quiz, score, questions.size());
        return saveQuizResult(result);
    }

    public Quiz createQuiz(String title, String description, Quiz.DifficultyLevel difficulty, Integer timeLimitMinutes) {
        Quiz quiz = new Quiz(title, description);
        quiz.setDifficulty(difficulty);
        quiz.setTimeLimitMinutes(timeLimitMinutes);
        return saveQuiz(quiz);
    }
}
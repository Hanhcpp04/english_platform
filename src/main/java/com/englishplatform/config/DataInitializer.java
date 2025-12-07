package com.englishplatform.config;

import com.englishplatform.model.*;
import com.englishplatform.service.UserService;
import com.englishplatform.service.VocabularyService;
import com.englishplatform.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private VocabularyService vocabularyService;

    @Autowired
    private QuizService quizService;

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample data only if database is empty
        if (vocabularyService.getVocabularyCount() == 0) {
            initializeVocabulary();
            initializeQuizzes();
            initializeUsers();
        }
    }

    private void initializeVocabulary() {
        // Beginner Level Vocabulary
        vocabularyService.createVocabulary(
            "hello", 
            "A greeting used when meeting someone", 
            "/həˈloʊ/", 
            "Hello, how are you today?", 
            Vocabulary.DifficultyLevel.BEGINNER, 
            Vocabulary.WordType.INTERJECTION
        );

        vocabularyService.createVocabulary(
            "book", 
            "A written or printed work consisting of pages", 
            "/bʊk/", 
            "I am reading a book about English grammar.", 
            Vocabulary.DifficultyLevel.BEGINNER, 
            Vocabulary.WordType.NOUN
        );

        vocabularyService.createVocabulary(
            "run", 
            "To move quickly on foot", 
            "/rʌn/", 
            "I run in the park every morning.", 
            Vocabulary.DifficultyLevel.BEGINNER, 
            Vocabulary.WordType.VERB
        );

        vocabularyService.createVocabulary(
            "beautiful", 
            "Pleasing to the senses or mind aesthetically", 
            "/ˈbjuːtɪfəl/", 
            "The sunset is beautiful tonight.", 
            Vocabulary.DifficultyLevel.BEGINNER, 
            Vocabulary.WordType.ADJECTIVE
        );

        vocabularyService.createVocabulary(
            "quickly", 
            "At a fast speed; rapidly", 
            "/ˈkwɪkli/", 
            "She completed the task quickly.", 
            Vocabulary.DifficultyLevel.BEGINNER, 
            Vocabulary.WordType.ADVERB
        );

        // Intermediate Level Vocabulary
        vocabularyService.createVocabulary(
            "accomplish", 
            "To achieve or complete successfully", 
            "/əˈkʌmplɪʃ/", 
            "She was able to accomplish all her goals.", 
            Vocabulary.DifficultyLevel.INTERMEDIATE, 
            Vocabulary.WordType.VERB
        );

        vocabularyService.createVocabulary(
            "magnificent", 
            "Impressively beautiful, elaborate, or extravagant", 
            "/mæɡˈnɪfɪsənt/", 
            "The palace has a magnificent architecture.", 
            Vocabulary.DifficultyLevel.INTERMEDIATE, 
            Vocabulary.WordType.ADJECTIVE
        );

        vocabularyService.createVocabulary(
            "opportunity", 
            "A set of circumstances that makes it possible to do something", 
            "/ˌɑːpərˈtuːnəti/", 
            "This job offers a great opportunity for growth.", 
            Vocabulary.DifficultyLevel.INTERMEDIATE, 
            Vocabulary.WordType.NOUN
        );

        // Advanced Level Vocabulary
        vocabularyService.createVocabulary(
            "ubiquitous", 
            "Present, appearing, or found everywhere", 
            "/juːˈbɪkwɪtəs/", 
            "Smartphones have become ubiquitous in modern society.", 
            Vocabulary.DifficultyLevel.ADVANCED, 
            Vocabulary.WordType.ADJECTIVE
        );

        vocabularyService.createVocabulary(
            "ephemeral", 
            "Lasting for a very short time", 
            "/ɪˈfemərəl/", 
            "The beauty of cherry blossoms is ephemeral.", 
            Vocabulary.DifficultyLevel.ADVANCED, 
            Vocabulary.WordType.ADJECTIVE
        );
    }

    private void initializeQuizzes() {
        // Beginner Quiz
        Quiz beginnerQuiz = quizService.createQuiz(
            "Basic English Vocabulary", 
            "Test your knowledge of basic English words", 
            Quiz.DifficultyLevel.BEGINNER, 
            10
        );

        // Add questions to beginner quiz
        QuizQuestion q1 = new QuizQuestion(
            "What does 'hello' mean?",
            "A greeting",
            "A question",
            "A statement",
            "An answer",
            "A"
        );
        q1.setQuiz(beginnerQuiz);
        q1.setExplanation("Hello is a common greeting used when meeting someone.");
        quizService.saveQuizQuestion(q1);

        QuizQuestion q2 = new QuizQuestion(
            "Which word means 'to move quickly on foot'?",
            "Walk",
            "Run",
            "Jump",
            "Crawl",
            "B"
        );
        q2.setQuiz(beginnerQuiz);
        q2.setExplanation("Run means to move quickly on foot, faster than walking.");
        quizService.saveQuizQuestion(q2);

        // Intermediate Quiz
        Quiz intermediateQuiz = quizService.createQuiz(
            "Intermediate Vocabulary Challenge", 
            "Challenge yourself with intermediate level words", 
            Quiz.DifficultyLevel.INTERMEDIATE, 
            15
        );

        QuizQuestion q3 = new QuizQuestion(
            "What does 'accomplish' mean?",
            "To fail",
            "To try",
            "To achieve successfully",
            "To begin",
            "C"
        );
        q3.setQuiz(intermediateQuiz);
        q3.setExplanation("Accomplish means to achieve or complete something successfully.");
        quizService.saveQuizQuestion(q3);

        // Advanced Quiz
        Quiz advancedQuiz = quizService.createQuiz(
            "Advanced English Mastery", 
            "Test your advanced English vocabulary skills", 
            Quiz.DifficultyLevel.ADVANCED, 
            20
        );

        QuizQuestion q4 = new QuizQuestion(
            "What does 'ubiquitous' mean?",
            "Very rare",
            "Present everywhere",
            "Extremely large",
            "Very small",
            "B"
        );
        q4.setQuiz(advancedQuiz);
        q4.setExplanation("Ubiquitous means present, appearing, or found everywhere.");
        quizService.saveQuizQuestion(q4);
    }

    private void initializeUsers() {
        // Create a demo user for testing
        if (!userService.existsByUsername("demo")) {
            User demoUser = userService.registerUser(
                "demo", 
                "demo@englishplatform.com", 
                "password", 
                "Demo", 
                "User"
            );
            demoUser.setRole(User.Role.USER);
            userService.updateUser(demoUser);
        }

        // Create an admin user
        if (!userService.existsByUsername("admin")) {
            User adminUser = userService.registerUser(
                "admin", 
                "admin@englishplatform.com", 
                "password", 
                "Admin", 
                "User"
            );
            adminUser.setRole(User.Role.ADMIN);
            userService.updateUser(adminUser);
        }
    }
}
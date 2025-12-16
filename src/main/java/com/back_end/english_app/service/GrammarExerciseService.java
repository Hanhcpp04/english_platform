package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.grammar.*;
import com.back_end.english_app.entity.*;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrammarExerciseService {

    private final GrammarQuestionRepository grammarQuestionRepository;
    private final UserGrammarExerciseAnswerRepository answerRepository;
    private final UserGrammarProgressRepository progressRepository;
    private final ExerciseGrammarTypeRepository exerciseTypeRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final GrammarLessonRepository grammarLessonRepository;

    /**
     * API 0: Lấy danh sách Exercise Types theo topic và lesson
     */
    public List<ExerciseTypeStructureDTO> getExerciseTypes(Long topicId, Long lessonId) {
        log.info("Getting exercise types for topic: {}, lesson: {}", topicId, lessonId);

        List<ExerciseGrammarTypeEntity> exerciseTypes = exerciseTypeRepository.findByTopicIdAndIsActiveTrue(topicId);

        return exerciseTypes.stream()
                .map(type -> convertToExerciseTypeStructureDTO(type, lessonId))
                .collect(Collectors.toList());
    }

    /**
     * API 1: Lấy danh sách câu hỏi theo topic, lesson và type
     */
    public GrammarExerciseResponseDTO getQuestions(Long topicId, Long lessonId, Long typeId) {
        log.info("Getting questions for topic: {}, lesson: {}, type: {}", topicId, lessonId, typeId);

        // Validate lesson và lấy topicId thực tế từ lesson
        GrammarLessonEntity lesson = grammarLessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        Long actualTopicId = lesson.getTopic().getId();

        // Validate nếu topicId được truyền vào có khớp với topic thực tế của lesson
        if (topicId != null && !topicId.equals(actualTopicId)) {
            throw new IllegalArgumentException(
                    String.format("Lesson %d does not belong to topic %d. It belongs to topic %d",
                            lessonId, topicId, actualTopicId));
        }

        // Lấy danh sách câu hỏi từ database
        List<GrammarQuestionEntity> questions = grammarQuestionRepository.findByLessonIdAndTypeIdAndIsActiveTrue(
                lessonId, typeId
        );

        if (questions.isEmpty()) {
            log.warn("No questions found for lesson: {}, type: {}", lessonId, typeId);
        }

        // Convert sang DTO
        List<GrammarQuestionDTO> questionDTOs = questions.stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());

        return GrammarExerciseResponseDTO.builder()
                .topicId(actualTopicId) // Sử dụng topicId thực tế từ lesson
                .lessonId(lessonId)
                .typeId(typeId)
                .questions(questionDTOs)
                .build();
    }

    /**
     * API 2: Submit câu trả lời và lưu tiến trình
     */
    @Transactional
    public SubmitAnswerResponseDTO submitAnswer(Long userId, Long questionId, Long typeId, String userAnswer) {
        log.info("User {} submitting answer for question {}", userId, questionId);

        // Validate user
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Lấy câu hỏi
        GrammarQuestionEntity question = grammarQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        // Lấy type
        ExerciseGrammarTypeEntity type = exerciseTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found with id: " + typeId));

        // Kiểm tra câu trả lời dựa trên loại bài tập
        boolean isCorrect = checkAnswerByType(userAnswer, question, type);

        // Lưu câu trả lời của user
        UserGrammarExerciseAnswerEntity answerEntity = new UserGrammarExerciseAnswerEntity();
        answerEntity.setUser(user);
        answerEntity.setQuestion(question);
        answerEntity.setType(type);
        answerEntity.setUserAnswer(userAnswer.trim());
        answerEntity.setIsCorrect(isCorrect);
        answerEntity.setAttemptedAt(LocalDateTime.now());

        answerRepository.save(answerEntity);
        log.info("Saved answer for user {} - question {} - correct: {}", userId, questionId, isCorrect);

        // Cộng XP nếu trả lời đúng
        int xpEarned = 0;
        if (isCorrect) {
            xpEarned = question.getXpReward() != null ? question.getXpReward() : 5;
            userService.addXP(userId, xpEarned);
            log.info("Added {} XP to user {}", xpEarned, userId);
        }

        // Cập nhật tiến trình (nếu chưa có)
        updateUserProgress(user, question);

        return SubmitAnswerResponseDTO.builder()
                .isCorrect(isCorrect)
                .correctAnswer(question.getCorrectAnswer())
                .xpEarned(xpEarned)
                .message(isCorrect ? "Correct! Well done!" : "Incorrect. Try again!")
                .build();
    }

    /**
     * API 3: Lấy lịch sử câu trả lời của user
     */
    public UserAnswerHistoryResponseDTO getAnswerHistory(Long userId, Long topicId, Long lessonId, Long typeId) {
        log.info("Getting answer history for user: {}, topic: {}, lesson: {}, type: {}",
                userId, topicId, lessonId, typeId);

        // Validate lesson và lấy topicId thực tế
        GrammarLessonEntity lesson = grammarLessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        Long actualTopicId = lesson.getTopic().getId();

        // Validate nếu topicId được truyền vào có khớp với topic thực tế của lesson
        if (topicId != null && !topicId.equals(actualTopicId)) {
            throw new IllegalArgumentException(
                    String.format("Lesson %d does not belong to topic %d. It belongs to topic %d",
                            lessonId, topicId, actualTopicId));
        }

        // Lấy danh sách câu trả lời
        List<UserGrammarExerciseAnswerEntity> answers = answerRepository.findAnswersByUserLessonAndType(
                userId, lessonId, typeId
        );

        // Convert sang DTO
        List<UserAnswerDTO> answerDTOs = answers.stream()
                .map(this::convertToAnswerDTO)
                .collect(Collectors.toList());

        // Đếm số câu đúng
        int correctCount = (int) answers.stream()
                .filter(UserGrammarExerciseAnswerEntity::getIsCorrect)
                .count();

        return UserAnswerHistoryResponseDTO.builder()
                .topicId(actualTopicId) // Sử dụng topicId thực tế từ lesson
                .lessonId(lessonId)
                .typeId(typeId)
                .totalQuestions(answers.size())
                .correctAnswers(correctCount)
                .answers(answerDTOs)
                .build();
    }

    /**
     * API 4: Reset câu trả lời của user theo lesson và type
     * Xóa tất cả câu trả lời đã làm trong một loại bài tập cụ thể của một lesson
     */
    @Transactional
    public void resetExerciseAnswers(Long userId, Long lessonId, Long typeId) {
        log.info("Resetting exercise answers for user: {}, lesson: {}, type: {}", userId, lessonId, typeId);

        // Validate user
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate lesson
        grammarLessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        // Validate type
        exerciseTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found with id: " + typeId));

        // Kiểm tra xem có câu trả lời nào không
        List<UserGrammarExerciseAnswerEntity> existingAnswers = answerRepository.findAnswersByUserLessonAndType(
                userId, lessonId, typeId
        );

        if (existingAnswers.isEmpty()) {
            log.warn("No answers found to reset for user: {}, lesson: {}, type: {}", userId, lessonId, typeId);
            throw new ResourceNotFoundException("No answers found to reset");
        }

        // Xóa tất cả câu trả lời
        answerRepository.deleteByUserIdAndLessonIdAndTypeId(userId, lessonId, typeId);

        log.info("Successfully reset {} answers for user: {}, lesson: {}, type: {}",
                existingAnswers.size(), userId, lessonId, typeId);
    }

    /**
     * API 5: Lấy thống kê độ chính xác và số câu đúng/sai
     */
    public ExerciseAccuracyStatsDTO getExerciseAccuracyStats(Long userId, Long lessonId, Long typeId) {
        log.info("Getting accuracy stats for user: {}, lesson: {}, type: {}", userId, lessonId, typeId);

        // Validate user
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Validate lesson
        grammarLessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + lessonId));

        // Validate và lấy thông tin exercise type
        ExerciseGrammarTypeEntity exerciseType = exerciseTypeRepository.findById(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise type not found with id: " + typeId));

        // Lấy thống kê từ database
        Long totalAnswers = answerRepository.countTotalAnswers(userId, lessonId, typeId);
        Long correctAnswers = answerRepository.countCorrectAnswers(userId, lessonId, typeId);
        Long incorrectAnswers = answerRepository.countIncorrectAnswers(userId, lessonId, typeId);
        Long distinctQuestions = answerRepository.countDistinctAnsweredQuestions(userId, lessonId, typeId);

        // Lấy tổng số câu hỏi có sẵn trong bài tập
        Integer totalQuestionsAvailable = grammarQuestionRepository
                .countByLessonIdAndTypeId(lessonId, typeId);

        // Tính toán accuracy rate
        double accuracyRate = 0.0;
        if (totalAnswers > 0) {
            accuracyRate = (correctAnswers * 100.0) / totalAnswers;
        }

        // Tính toán completion rate
        double completionRate = 0.0;
        if (totalQuestionsAvailable > 0) {
            completionRate = (distinctQuestions * 100.0) / totalQuestionsAvailable;
        }

        // Xếp loại độ chính xác
        String accuracyGrade = calculateAccuracyGrade(accuracyRate);

        return ExerciseAccuracyStatsDTO.builder()
                .userId(userId)
                .lessonId(lessonId)
                .typeId(typeId)
                .typeName(exerciseType.getName())
                .totalAnswers(totalAnswers)
                .correctAnswers(correctAnswers)
                .incorrectAnswers(incorrectAnswers)
                .distinctQuestions(distinctQuestions)
                .accuracyRate(Math.round(accuracyRate * 100.0) / 100.0) // Làm tròn 2 chữ số thập phân
                .accuracyGrade(accuracyGrade)
                .totalQuestionsAvailable(totalQuestionsAvailable)
                .completionRate(Math.round(completionRate * 100.0) / 100.0) // Làm tròn 2 chữ số thập phân
                .build();
    }

    /**
     * Xếp loại độ chính xác dựa trên tỷ lệ phần trăm
     */
    private String calculateAccuracyGrade(Double accuracyRate) {
        if (accuracyRate >= 90) {
            return "Excellent"; // Xuất sắc
        } else if (accuracyRate >= 75) {
            return "Good"; // Tốt
        } else if (accuracyRate >= 60) {
            return "Average"; // Trung bình
        } else if (accuracyRate >= 40) {
            return "Fair"; // Khá
        } else {
            return "Poor"; // Yếu
        }
    }

    // Helper methods
    private GrammarQuestionDTO convertToQuestionDTO(GrammarQuestionEntity entity) {
        return GrammarQuestionDTO.builder()
                .id(entity.getId())
                .question(entity.getQuestion())
                .options(entity.getOptions())
                .typeId(entity.getType().getId())
                .xpReward(entity.getXpReward())
                .build();
    }

    private UserAnswerDTO convertToAnswerDTO(UserGrammarExerciseAnswerEntity entity) {
        return UserAnswerDTO.builder()
                .questionId(entity.getQuestion().getId())
                .question(entity.getQuestion().getQuestion())
                .userAnswer(entity.getUserAnswer())
                .correctAnswer(entity.getQuestion().getCorrectAnswer())
                .isCorrect(entity.getIsCorrect())
                .attemptedAt(entity.getAttemptedAt())
                .typeId(entity.getType().getId())
                .xpReward(entity.getQuestion().getXpReward())
                .build();
    }

    /**
     * Kiểm tra câu trả lời dựa trên loại bài tập
     */
    private boolean checkAnswerByType(String userAnswer, GrammarQuestionEntity question, ExerciseGrammarTypeEntity type) {
        String typeName = type.getName();
        log.info("Checking answer for type: {}", typeName);

        if ("Multiple Choice".equalsIgnoreCase(typeName)) {
            return checkMultipleChoiceAnswer(userAnswer, question);
        } else if ("Fill in the Blank".equalsIgnoreCase(typeName)) {
            return checkFillInTheBlankAnswer(userAnswer, question.getCorrectAnswer());
        } else {
            // Fallback: so sánh đơn giản
            log.warn("Unknown exercise type: {}, using simple comparison", typeName);
            return checkSimpleAnswer(userAnswer, question.getCorrectAnswer());
        }
    }

    /**
     * Kiểm tra câu trả lời cho bài tập Multiple Choice
     */
    private boolean checkMultipleChoiceAnswer(String userAnswer, GrammarQuestionEntity question) {
        String correctAnswer = question.getCorrectAnswer();

        // Normalize cả hai câu trả lời (trim, lowercase)
        String normalizedUserAnswer = userAnswer.trim().toLowerCase();
        String normalizedCorrectAnswer = correctAnswer.trim().toLowerCase();

        log.info("Multiple Choice - User: '{}', Correct: '{}'", normalizedUserAnswer, normalizedCorrectAnswer);

        return normalizedUserAnswer.equals(normalizedCorrectAnswer);
    }

    /**
     * Kiểm tra câu trả lời cho bài tập Fill in the Blank
     * Xử lý các trường hợp: chia động từ, dấu câu, khoảng trắng, viết hoa/thường
     */
    private boolean checkFillInTheBlankAnswer(String userAnswer, String correctAnswer) {
        // Normalize: loại bỏ khoảng trắng thừa, chuyển thành lowercase
        String normalizedUserAnswer = normalizeAnswer(userAnswer);
        String normalizedCorrectAnswer = normalizeAnswer(correctAnswer);

        log.info("Fill in the Blank - User: '{}', Correct: '{}'", normalizedUserAnswer, normalizedCorrectAnswer);

        // So sánh chính xác
        if (normalizedUserAnswer.equals(normalizedCorrectAnswer)) {
            return true;
        }

        // Kiểm tra các trường hợp có dấu câu thừa
        String userAnswerWithoutPunctuation = removePunctuation(normalizedUserAnswer);
        String correctAnswerWithoutPunctuation = removePunctuation(normalizedCorrectAnswer);

        return userAnswerWithoutPunctuation.equals(correctAnswerWithoutPunctuation);
    }

    /**
     * So sánh đơn giản (fallback)
     */
    private boolean checkSimpleAnswer(String userAnswer, String correctAnswer) {
        String normalized1 = userAnswer.trim().toLowerCase();
        String normalized2 = correctAnswer.trim().toLowerCase();
        return normalized1.equals(normalized2);
    }

    /**
     * Normalize câu trả lời: trim, lowercase, loại bỏ khoảng trắng thừa
     */
    private String normalizeAnswer(String answer) {
        if (answer == null) {
            return "";
        }
        // Loại bỏ khoảng trắng thừa và chuyển thành lowercase
        return answer.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    /**
     * Loại bỏ dấu câu khỏi chuỗi
     */
    private String removePunctuation(String text) {
        if (text == null) {
            return "";
        }
        // Loại bỏ các ký tự dấu câu phổ biến
        return text.replaceAll("[.,!?;:'\"-]", "").trim();
    }

    private void updateUserProgress(UserEntity user, GrammarQuestionEntity question) {
        // Kiểm tra xem đã có progress chưa
        UserGrammarProgressEntity progress = new UserGrammarProgressEntity();
        progress.setUser(user);
        progress.setTopic(question.getLesson().getTopic());
        progress.setLesson(question.getLesson());
        progress.setQuestion(question);
        progress.setType(UserGrammarProgressEntity.ProgressType.exercise);
        progress.setIsCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        progressRepository.save(progress);
        log.info("Updated progress for user {} - lesson {} - question {}",
                user.getId(), question.getLesson().getId(), question.getId());
    }

    private ExerciseTypeStructureDTO convertToExerciseTypeStructureDTO(ExerciseGrammarTypeEntity type, Long lessonId) {
        Integer totalQuestions = grammarQuestionRepository.countByLessonIdAndTypeId(lessonId, type.getId());

        return ExerciseTypeStructureDTO.builder()
                .id(type.getId())
                .name(type.getName())
                .description(type.getDescription())
                .totalQuestions(totalQuestions != null ? totalQuestions : 0)
                .build();
    }
}


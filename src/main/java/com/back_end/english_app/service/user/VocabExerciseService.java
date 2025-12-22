package com.back_end.english_app.service.user;
import com.back_end.english_app.dto.request.vocabExercise.SubmitAnswerRequest;
import com.back_end.english_app.dto.respones.vocabExercise.*;
import com.back_end.english_app.entity.*;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabExerciseService {

    private final VocabExerciseRepository repository;
    private final BadgeCheckService badgeCheckService;
    private final UserVocabExerciseAnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final VocabExerciseQuestionRepository questionRepository;
    private final VocabExerciseTypeRepository typeRepository;
    private final VocabTopicRepository topicRepository;
    private final UserDailyStatsService userDailyStatsService;

    // lấy câu hỏi theo từng loại và thuộc về topic nào của user nào
    public List<ExerciseTypeDTO> getExerciseTypesByTopic(Integer topicId, Integer userId) {
        List<ExerciseTypeDTO> exerciseTypes = repository.getExerciseTypesByTopic(topicId, userId);

        if (exerciseTypes == null || exerciseTypes.isEmpty()) {
            throw new ResourceNotFoundException("Exercise types", "topicId", topicId);
        }

        return exerciseTypes;
    }

    // 2. lấy các câu hỏi theo loại câu hỏi nào của từng topic và của user nào
    public QuestionsResponse getQuestionsByType(Integer typeId, Integer topicId, Integer userId) {
        QuestionsResponse response = repository.getQuestionsByTypeAndTopic(typeId, topicId, userId);

        if (response == null || response.getQuestions() == null || response.getQuestions().isEmpty()) {
            throw new ResourceNotFoundException(
                String.format("Questions not found for exerciseTypeId: %d and topicId: %d", typeId, topicId)
            );
        }
        return response;
    }

    // 3. khi người dùng nộp bài thì xử lí ở đây
    @Transactional
    public SubmitAnswerResponse submitAnswer(Integer questionId, SubmitAnswerRequest request) {
        QuestionDTO question = repository.getQuestionById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        boolean alreadyCompleted = repository.isQuestionCompletedByUser(request.getUserId(), questionId);
        boolean isCorrect = false;
        String correctAnswerDisplay;
        String exerciseTypeLower = request.getExerciseType().toLowerCase().trim();

        // Lấy entities để lưu lịch sử câu trả lời
        UserEntity user = userRepository.findById(request.getUserId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        VocabExerciseQuestionEntity questionEntity = questionRepository.findById(questionId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        VocabExerciseTypeEntity typeEntity = typeRepository.findById(request.getTypeId().longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise Type", "id", request.getTypeId()));

        // Lấy topicId (phải là final để dùng trong lambda nếu cần)
        final Integer topicId;
        if (request.getTopicId() == null) {
            topicId = repository.getTopicIdByQuestionId(questionId);
        } else {
            topicId = request.getTopicId();
        }
        VocabTopicEntity topicEntity = topicRepository.findById(topicId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

        if (exerciseTypeLower.contains("multiple") ||
            exerciseTypeLower.contains("trắc nghiệm") ||
            exerciseTypeLower.contains("choice")) {
            String userAnswer = request.getUserAnswer().trim();
            Integer correctIndex = question.getCorrectIndex();
            if (correctIndex == null) {
                System.err.println("ERROR: correctIndex is null for multiple choice question");
                correctAnswerDisplay = "N/A";
            } else {
                try {
                    int userIndex = Integer.parseInt(userAnswer);
                    isCorrect = userIndex == correctIndex;
                    correctAnswerDisplay = String.valueOf(correctIndex);
                } catch (NumberFormatException e) {
                    if (question.getOptions() != null && question.getOptions().isArray()) {
                        for (int i = 0; i < question.getOptions().size(); i++) {
                            String optionText = question.getOptions().get(i).asText();
                            if (optionText.trim().equalsIgnoreCase(userAnswer)) {
                                isCorrect = (i == correctIndex);
                                System.out.println("DEBUG: Matched user answer '" + userAnswer +
                                    "' to index " + i + ", correct index is " + correctIndex);
                                break;
                            }
                        }
                    }
                    if (question.getOptions() != null &&
                        question.getOptions().isArray() &&
                        correctIndex < question.getOptions().size()) {
                        correctAnswerDisplay = question.getOptions().get(correctIndex).asText();
                    } else {
                        correctAnswerDisplay = String.valueOf(correctIndex);
                    }
                }
            }
        } else if (exerciseTypeLower.contains("arrangement") ||
                   exerciseTypeLower.contains("sắp xếp") ||
                   exerciseTypeLower.contains("scramble") ||
                   exerciseTypeLower.contains("word")) {
            String correctAnswer = question.getCorrectAnswer();
            if (correctAnswer != null && !correctAnswer.trim().isEmpty()) {
                isCorrect = request.getUserAnswer().trim()
                        .equalsIgnoreCase(correctAnswer.trim());
                correctAnswerDisplay = correctAnswer;
            } else {
                correctAnswerDisplay = "";
            }
        } else {
            String correctAnswer = question.getCorrectAnswer();
            if (correctAnswer != null && !correctAnswer.trim().isEmpty()) {
                isCorrect = request.getUserAnswer().trim()
                        .equalsIgnoreCase(correctAnswer.trim());
                correctAnswerDisplay = correctAnswer;
            } else {
                correctAnswerDisplay = "";
            }
        }

        int xpEarned = 0;
        Integer totalXp = repository.getUserTotalXP(request.getUserId());

        if (totalXp == null) {
            totalXp = 0;
        }

        if (isCorrect) {
            if (!alreadyCompleted) {
                xpEarned = question.getXpReward() != null ? question.getXpReward() : 0;
                repository.updateUserXP(request.getUserId(), xpEarned);
                totalXp += xpEarned;

                repository.updateUserProgress(
                        request.getUserId(),
                        questionId,
                        topicId
                );

                // Cập nhật daily stats
                userDailyStatsService.recordExerciseDone(user, 1);
                if (xpEarned > 0) {
                    userDailyStatsService.recordXpEarned(user, xpEarned);
                }

                // Kiểm tra và cập nhật huy hiệu sau khi hoàn thành bài tập từ vựng
                try {
                    badgeCheckService.checkAndUpdateBadges(request.getUserId().longValue(), "vocabulary");
                    System.out.println("Badge check completed for user " + request.getUserId() + " after completing vocab exercise");
                } catch (Exception e) {
                    System.err.println("Error checking badges for user " + request.getUserId() + ": " + e.getMessage());
                    // Không throw exception để không ảnh hưởng đến flow chính
                }
            } else {
                System.out.println("DEBUG: Question " + questionId + " already completed. NO XP earned and NO progress update.");
            }
        }

        // LƯU LỊCH SỬ CÂU TRẢ LỜI (dù đúng hay sai, dù đã làm hay chưa)
        UserVocabExerciseAnswerEntity answerEntity = new UserVocabExerciseAnswerEntity();
        answerEntity.setUser(user);
        answerEntity.setQuestion(questionEntity);
        answerEntity.setType(typeEntity);
        answerEntity.setTopic(topicEntity);
        answerEntity.setUserAnswer(request.getUserAnswer().trim());
        answerEntity.setIsCorrect(isCorrect);
        answerEntity.setXpEarned(xpEarned);
        answerEntity.setAttemptedAt(LocalDateTime.now());
        answerRepository.save(answerEntity);

        System.out.println("DEBUG: Answer history saved for user " + request.getUserId() +
                         ", question " + questionId + ", isCorrect: " + isCorrect);

        Integer typeId = request.getTypeId();
        if (typeId == null) {
            throw new ResourceNotFoundException("TypeId must be provided in request");
        }

        ProgressDetail progress = repository.getProgressDetail(typeId, request.getUserId());
        String explanation = isCorrect
                ? "Đúng rồi! Làm tốt lắm!"
                : String.format("Sai rồi. Đáp án đúng là: %s", correctAnswerDisplay);

        return new SubmitAnswerResponse(
                isCorrect,
                correctAnswerDisplay,
                xpEarned,
                totalXp,
                explanation,
                progress,
                alreadyCompleted
        );
    }

    // 5. Lấy tiến độ của topic
    public TopicProgressResponse getTopicProgress(Integer topicId, Integer userId) {
        String topicName = repository.getTopicName(topicId);

        if (topicName == null || topicName.isEmpty()) {
            throw new ResourceNotFoundException("Topic", "id", topicId);
        }

        OverallProgressDTO overallProgress = repository.getOverallProgress(topicId, userId);
        List<ExerciseTypeProgressDTO> exerciseTypes = repository.getExerciseTypeProgress(topicId, userId);

        if (exerciseTypes == null || exerciseTypes.isEmpty()) {
            throw new ResourceNotFoundException("Exercise type progress", "topicId", topicId);
        }

        return new TopicProgressResponse(topicId, topicName, overallProgress, exerciseTypes);
    }

    // 6. Đặt lại tiến độ
    @Transactional
    public void resetProgress(Integer typeId, Integer userId) {
        repository.resetProgress(typeId, userId);
    }

    /**
     * 7. Lấy lịch sử câu trả lời của user theo topic và type
     */
    public VocabAnswerHistoryResponseDTO getAnswerHistory(Integer userId, Integer topicId, Integer typeId) {
        // Xác thực
        userRepository.findById(userId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        VocabTopicEntity topic = topicRepository.findById(topicId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
        VocabExerciseTypeEntity type = typeRepository.findById(typeId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise Type", "id", typeId));

        // Lấy lịch sử câu trả lời
        List<UserVocabExerciseAnswerEntity> answerEntities = answerRepository
                .findAnswersByUserTopicAndType(userId.longValue(), topicId.longValue(), typeId.longValue());

        // Chuyển đổi sang DTOs
        List<VocabAnswerHistoryDTO> answerDTOs = answerEntities.stream()
                .map(answer -> VocabAnswerHistoryDTO.builder()
                        .questionId(answer.getQuestion().getId())
                        .question(answer.getQuestion().getQuestion())
                        .userAnswer(answer.getUserAnswer())
                        .correctAnswer(answer.getQuestion().getCorrectAnswer())
                        .isCorrect(answer.getIsCorrect())
                        .xpEarned(answer.getXpEarned())
                        .attemptedAt(answer.getAttemptedAt())
                        .build())
                .collect(Collectors.toList());

        // Đếm thống kê
        int correctCount = (int) answerEntities.stream()
                .filter(UserVocabExerciseAnswerEntity::getIsCorrect)
                .count();
        int totalXp = answerEntities.stream()
                .mapToInt(UserVocabExerciseAnswerEntity::getXpEarned)
                .sum();

        return VocabAnswerHistoryResponseDTO.builder()
                .topicId(topicId.longValue())
                .topicName(topic.getName())
                .typeId(typeId.longValue())
                .typeName(type.getName())
                .totalAttempts(answerDTOs.size())
                .correctAnswers(correctCount)
                .totalXpEarned(totalXp)
                .answers(answerDTOs)
                .build();
    }

    /**
     * 8. Reset bài tập - xóa cả lịch sử câu trả lời và progress
     */
    @Transactional
    public void resetExerciseAnswers(Integer userId, Integer topicId, Integer typeId) {
        // Xác thực
        userRepository.findById(userId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        topicRepository.findById(topicId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
        typeRepository.findById(typeId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise Type", "id", typeId));

        // Kiểm tra có câu trả lời không
        List<UserVocabExerciseAnswerEntity> answers = answerRepository
                .findAnswersByUserTopicAndType(userId.longValue(), topicId.longValue(), typeId.longValue());

        if (answers == null || answers.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No answers found for user " + userId + " in topic " + topicId + " and type " + typeId);
        }

        // Xóa lịch sử câu trả lời
        answerRepository.deleteByUserIdAndTopicIdAndTypeId(
                userId.longValue(), topicId.longValue(), typeId.longValue());

        // Xóa tiến độ (nếu có)
        try {
            repository.resetProgress(typeId, userId);
        } catch (Exception e) {
            System.err.println("Warning: Could not reset progress: " + e.getMessage());
        }

        System.out.println("Successfully reset exercise answers for user " + userId +
                         ", topic " + topicId + ", type " + typeId);
    }

    /**
     * 9. Thống kê độ chính xác
     */
    public VocabAccuracyStatsDTO getExerciseAccuracyStats(Integer userId, Integer topicId, Integer typeId) {
        // Xác thực
        userRepository.findById(userId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        VocabTopicEntity topic = topicRepository.findById(topicId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));
        VocabExerciseTypeEntity type = typeRepository.findById(typeId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("Exercise Type", "id", typeId));

        // Truy vấn thống kê
        Long totalAttempts = answerRepository.countTotalAnswers(
                userId.longValue(), topicId.longValue(), typeId.longValue());
        Long correctAnswers = answerRepository.countCorrectAnswers(
                userId.longValue(), topicId.longValue(), typeId.longValue());
        Long incorrectAnswers = answerRepository.countIncorrectAnswers(
                userId.longValue(), topicId.longValue(), typeId.longValue());
        Long distinctQuestions = answerRepository.countDistinctAnsweredQuestions(
                userId.longValue(), topicId.longValue(), typeId.longValue());
        Long totalXpEarned = answerRepository.sumXpEarned(
                userId.longValue(), topicId.longValue(), typeId.longValue());

        // Lấy tổng số câu hỏi có sẵn (từ repository hoặc dùng phương án dự phòng)
        Integer totalQuestionsAvailable;
        try {
            totalQuestionsAvailable = questionRepository.countByTypeId(typeId.longValue());
        } catch (Exception e) {
            // Dự phòng: dùng số câu hỏi khác nhau đã làm nếu truy vấn đếm thất bại
            totalQuestionsAvailable = distinctQuestions.intValue();
        }

        // Tính các tỉ lệ
        double accuracyRate = 0.0;
        if (totalAttempts > 0) {
            accuracyRate = Math.round((correctAnswers * 100.0 / totalAttempts) * 100.0) / 100.0;
        }

        double completionRate = 0.0;
        if (totalQuestionsAvailable > 0) {
            completionRate = Math.round((distinctQuestions * 100.0 / totalQuestionsAvailable) * 100.0) / 100.0;
        }

        // Xác định xếp loại
        String accuracyGrade = calculateGrade(accuracyRate);

        return VocabAccuracyStatsDTO.builder()
                .userId(userId.longValue())
                .topicId(topicId.longValue())
                .topicName(topic.getName())
                .typeId(typeId.longValue())
                .typeName(type.getName())
                .totalAttempts(totalAttempts)
                .correctAnswers(correctAnswers)
                .incorrectAnswers(incorrectAnswers)
                .distinctQuestions(distinctQuestions)
                .accuracyRate(accuracyRate)
                .accuracyGrade(accuracyGrade)
                .totalQuestionsAvailable(totalQuestionsAvailable)
                .completionRate(completionRate)
                .totalXpEarned(totalXpEarned)
                .build();
    }
    private String calculateGrade(double accuracyRate) {
        if (accuracyRate >= 90.0) {
            return "Excellent";
        } else if (accuracyRate >= 75.0) {
            return "Good";
        } else if (accuracyRate >= 60.0) {
            return "Average";
        } else if (accuracyRate >= 40.0) {
            return "Fair";
        } else {
            return "Poor";
        }
    }
}

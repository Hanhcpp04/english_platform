package com.back_end.english_app.service;



import com.back_end.english_app.dto.request.vocabExercise.SubmitAnswerRequest;
import com.back_end.english_app.dto.request.vocabExercise.SubmitBatchRequest;
import com.back_end.english_app.dto.respones.vocabExercise.*;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.VocabExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VocabExerciseService {

    private final VocabExerciseRepository repository;

    // 1. Get exercise types by topic
    public List<ExerciseTypeDTO> getExerciseTypesByTopic(Integer topicId, Integer userId) {
        List<ExerciseTypeDTO> exerciseTypes = repository.getExerciseTypesByTopic(topicId, userId);

        if (exerciseTypes == null || exerciseTypes.isEmpty()) {
            throw new ResourceNotFoundException("Exercise types", "topicId", topicId);
        }

        return exerciseTypes;
    }

    // 2. Get questions by type
    public QuestionsResponse getQuestionsByType(Integer typeId, Integer topicId, Integer userId) {
        QuestionsResponse response = repository.getQuestionsByTypeAndTopic(typeId, topicId, userId);

        if (response == null || response.getQuestions() == null || response.getQuestions().isEmpty()) {
            throw new ResourceNotFoundException(
                String.format("Questions not found for exerciseTypeId: %d and topicId: %d", typeId, topicId)
            );
        }

        // Remove sensitive data
//        response.getQuestions().forEach(q -> {
//            q.setCorrectAnswer(null);
//            q.setCorrectIndex(null);
//        });

        return response;
    }

    // 3. Submit answer
    @Transactional
    public SubmitAnswerResponse submitAnswer(Integer questionId, SubmitAnswerRequest request) {
        QuestionDTO question = repository.getQuestionById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        // ✅ NEW: Check if question is already completed by user
        boolean alreadyCompleted = repository.isQuestionCompletedByUser(request.getUserId(), questionId);

        boolean isCorrect = false;
        String correctAnswerDisplay = "";
        String exerciseTypeLower = request.getExerciseType().toLowerCase().trim();

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
            // ✅ NEW: Only add XP if question is NOT already completed
            if (!alreadyCompleted) {
                xpEarned = question.getXpReward() != null ? question.getXpReward() : 0;
                repository.updateUserXP(request.getUserId(), xpEarned);
                totalXp += xpEarned;

                System.out.println("DEBUG: Question " + questionId + " is NEW. XP earned: " + xpEarned);

                // Get topicId from request
                Integer topicId = request.getTopicId();
                if (topicId == null) {
                    topicId = repository.getTopicIdByQuestionId(questionId);
                }

                // ✅ Chỉ cập nhật progress khi câu CHƯA hoàn thành
                repository.updateUserProgress(
                        request.getUserId(),
                        questionId,
                        topicId
                );
            } else {
                System.out.println("DEBUG: Question " + questionId + " already completed. NO XP earned and NO progress update.");
            }
        }

        Integer typeId = request.getTypeId();
        if (typeId == null) {
            throw new ResourceNotFoundException("TypeId must be provided in request");
        }

        ProgressDetail progress = repository.getProgressDetail(typeId, request.getUserId());
        String explanation = isCorrect
                ? "Correct! Well done!"
                : String.format("Incorrect. The correct answer is: %s", correctAnswerDisplay);

        return new SubmitAnswerResponse(
                isCorrect,
                correctAnswerDisplay,
                xpEarned,
                totalXp,
                explanation,
                progress,
                alreadyCompleted  // ✅ NEW: Truyền trạng thái câu đã hoàn thành
        );
    }
    // 5. Get topic progress
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

    // 6. Reset progress
    @Transactional
    public void resetProgress(Integer typeId, Integer userId) {
        repository.resetProgress(typeId, userId);
    }
}

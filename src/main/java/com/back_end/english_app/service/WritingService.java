package com.back_end.english_app.service;

import com.back_end.english_app.dto.request.writing.SubmitWritingRequest;
import com.back_end.english_app.dto.respones.writing.GradingResultResponse;
import com.back_end.english_app.dto.respones.writing.WritingPromptResponse;
import com.back_end.english_app.dto.respones.writing.WritingTaskResponse;
import com.back_end.english_app.dto.respones.writing.WritingTopicResponse;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.WritingPromptEntity;
import com.back_end.english_app.entity.WritingTopicEntity;
import com.back_end.english_app.entity.WrittingTaskEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.repository.WritingPromptRepository;
import com.back_end.english_app.repository.WritingTaskRepository;
import com.back_end.english_app.repository.WritingTopicRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WritingService {

    private final WritingTopicRepository writingTopicRepository;
    private final WritingTaskRepository writingTaskRepository;
    private final WritingPromptRepository writingPromptRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final BadgeCheckService badgeCheckService;
    private final ObjectMapper objectMapper;
    private final UserDailyStatsService userDailyStatsService;

    /**
     * Get all active writing topics
     * @return List of WritingTopicResponse
     */
    @Transactional(readOnly = true)
    public List<WritingTopicResponse> getAllTopics() {
        log.info("Fetching all active writing topics");

        List<WritingTopicEntity> topics = writingTopicRepository.findAllActiveTopics();

        return topics.stream()
                .map(topic -> new WritingTopicResponse(
                        topic.getId(),
                        topic.getName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get all tasks for a specific topic
     * @param topicId The topic ID
     * @return List of WritingTaskResponse
     */
    @Transactional(readOnly = true)
    public List<WritingTaskResponse> getTasksByTopicId(Integer topicId) {
        log.info("Fetching tasks for topic ID: {}", topicId);

        // Validate topic exists
        writingTopicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found with ID: " + topicId));

        List<WrittingTaskEntity> tasks = writingTaskRepository.findAllByTopicId(topicId);

        return tasks.stream()
                .map(task -> new WritingTaskResponse(
                        task.getId(),
                        task.getQuestion(),
                        task.getWritingTips(),
                        task.getXpReward()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get all prompts for a specific task and user
     * @param taskId The task ID
     * @param userId The user ID
     * @return List of WritingPromptResponse
     */
    @Transactional(readOnly = true)
    public List<WritingPromptResponse> getPromptsByTaskIdAndUserId(Integer taskId, Long userId) {
        log.info("Fetching prompts for task ID: {} and user ID: {}", taskId, userId);

        // Validate task exists
        writingTaskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with ID: " + taskId));

        List<WritingPromptEntity> prompts = writingPromptRepository.findAllByTaskIdAndUserId(taskId, userId);

        return prompts.stream()
                .map(prompt -> new WritingPromptResponse(
                        prompt.getId(),
                        prompt.getWritingTask().getId(),
                        prompt.getMode() != null ? prompt.getMode().name() : null,
                        prompt.getUserContent(),
                        prompt.getWordCount(),
                        prompt.getGrammarScore(),
                        prompt.getVocabularyScore(),
                        prompt.getCoherenceScore(),
                        prompt.getOverallScore(),
                        prompt.getAiFeedback(),
                        prompt.getGrammarSuggestions(),
                        prompt.getVocabularySuggestions(),
                        prompt.getTimeSpent(),
                        prompt.getXpReward(),
                        prompt.getIsCompleted(),
                        prompt.getSubmittedAt(),
                        prompt.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Submit and grade writing
     * @param request Writing submission request
     * @param userId User ID
     * @return Grading result
     */
    @Transactional
    public GradingResultResponse submitAndGradeWriting(SubmitWritingRequest request, Long userId) {
        log.info("Processing writing submission for user {} and task {}", userId, request.getTaskId());

        try {
            // 1. Validate user
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            // 2. Validate task
            WrittingTaskEntity task = writingTaskRepository.findById(request.getTaskId())
                    .orElseThrow(() -> new RuntimeException("Task not found with ID: " + request.getTaskId()));

            // 3. Call Gemini API to grade writing
            GradingResultResponse gradingResult = geminiService.gradeWriting(
                    request.getContent(),
                    task.getQuestion()
            );

            // 4. Calculate XP based on score
            int xpReward = calculateXpReward(gradingResult.getOverallScore(), task.getXpReward());
            gradingResult.setXpEarned(xpReward);

            // 5. Determine if completed (e.g., score >= 60)
            boolean isCompleted = gradingResult.getOverallScore() >= 60;
            gradingResult.setIsCompleted(isCompleted);

            // 6. Create and save WritingPromptEntity
            WritingPromptEntity prompt = new WritingPromptEntity();
            prompt.setUser(user);
            prompt.setWritingTask(task);
            prompt.setMode(parseMode(request.getMode()));
            prompt.setUserContent(request.getContent());
            prompt.setWordCount(gradingResult.getWordCount());
            prompt.setGrammarScore(gradingResult.getGrammarScore());
            prompt.setVocabularyScore(gradingResult.getVocabularyScore());
            prompt.setCoherenceScore(gradingResult.getCoherenceScore());
            prompt.setOverallScore(gradingResult.getOverallScore());
            prompt.setAiFeedback(gradingResult.getGeneralFeedback());
            prompt.setGrammarSuggestions(convertToJson(gradingResult.getGrammarSuggestions()));
            prompt.setVocabularySuggestions(convertToJson(gradingResult.getVocabularySuggestions()));
            prompt.setTimeSpent(request.getTimeSpent());
            prompt.setXpReward(xpReward);
            prompt.setIsCompleted(isCompleted);
            prompt.setSubmittedAt(LocalDateTime.now());

            WritingPromptEntity savedPrompt = writingPromptRepository.save(prompt);
            gradingResult.setPromptId(savedPrompt.getId());

            // 7. Award XP to user if completed
            if (isCompleted) {
                Integer currentXp = user.getTotalXp() != null ? user.getTotalXp() : 0;
                user.setTotalXp(currentXp + xpReward);
                userRepository.save(user);
                
                // Cập nhật daily stats
                userDailyStatsService.recordWritingSubmitted(user, 1);
                userDailyStatsService.recordXpEarned(user, xpReward);

                // 8. Check and update badges
                try {
                    badgeCheckService.checkAndUpdateBadges(userId, "WRITING");
                    log.info("Badge check completed for user {} after completing writing", userId);
                } catch (Exception e) {
                    log.error("Error checking badges for user {}: {}", userId, e.getMessage(), e);
                    // Don't throw - badge is secondary feature
                }
            }

            log.info("Writing graded successfully for user {}: overall score {}/100", userId, gradingResult.getOverallScore());
            return gradingResult;

        } catch (Exception e) {
            log.error("Error processing writing submission: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to grade writing: " + e.getMessage());
        }
    }

    /**
     * Calculate XP reward based on score
     */
    private int calculateXpReward(int score, Integer baseXp) {
        if (baseXp == null) {
            baseXp = 100; // Default XP
        }

        // Award XP based on score percentage
        if (score >= 90) {
            return (int) (baseXp * 1.5); // 150% for excellent work
        } else if (score >= 80) {
            return (int) (baseXp * 1.2); // 120% for good work
        } else if (score >= 60) {
            return baseXp; // 100% for passing work
        } else {
            return (int) (baseXp * 0.5); // 50% for effort
        }
    }

    /**
     * Parse mode string to enum
     */
    private WritingPromptEntity.Mode parseMode(String mode) {
        if (mode == null) {
            return WritingPromptEntity.Mode.PROMPT;
        }
        try {
            return WritingPromptEntity.Mode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            return WritingPromptEntity.Mode.PROMPT;
        }
    }

    /**
     * Convert list to JSON string
     */
    private String convertToJson(List<?> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Error converting to JSON: {}", e.getMessage());
            return "[]";
        }
    }
}


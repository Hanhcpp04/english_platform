package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.writing.WritingPromptResponse;
import com.back_end.english_app.dto.respones.writing.WritingTaskResponse;
import com.back_end.english_app.dto.respones.writing.WritingTopicResponse;
import com.back_end.english_app.entity.WritingPromptEntity;
import com.back_end.english_app.entity.WritingTopicEntity;
import com.back_end.english_app.entity.WrittingTaskEntity;
import com.back_end.english_app.repository.WritingPromptRepository;
import com.back_end.english_app.repository.WritingTaskRepository;
import com.back_end.english_app.repository.WritingTopicRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WritingService {

    private final WritingTopicRepository writingTopicRepository;
    private final WritingTaskRepository writingTaskRepository;
    private final WritingPromptRepository writingPromptRepository;

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
}


package com.back_end.english_app.service.user;

import com.back_end.english_app.dto.respones.grammar.GrammarStatsDTO;
import com.back_end.english_app.entity.GrammarTopicEntity;
import com.back_end.english_app.exception.BadRequestException;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.GrammarLessonRepository;
import com.back_end.english_app.repository.GrammarTopicRepository;
import com.back_end.english_app.repository.UserGrammarProgressRepository;
import com.back_end.english_app.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GrammarStatsService {

    UserGrammarProgressRepository userGrammarProgressRepository;
    GrammarTopicRepository grammarTopicRepository;
    GrammarLessonRepository grammarLessonRepository;
    UserRepository userRepository;

    @Transactional(readOnly = true)
    public GrammarStatsDTO getUserGrammarStats(Long userId) {
        // Validate input
        if (userId == null || userId <= 0) {
            log.error("Invalid userId: {}", userId);
            throw new BadRequestException("User ID must be a positive number");
        }

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            log.error("User not found with id: {}", userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        try {
            log.debug("Fetching grammar stats for userId: {}", userId);

            // Get total lessons learned
            Integer totalLessonLearned = userGrammarProgressRepository.countDistinctLessonsByUserIdAndIsCompletedTrue(userId);
            totalLessonLearned = (totalLessonLearned != null) ? totalLessonLearned : 0;

            // Get total XP earned
            Integer totalXpEarned = userGrammarProgressRepository.sumXpEarnedByUserId(userId);
            totalXpEarned = (totalXpEarned != null) ? totalXpEarned : 0;

            // Get completed topics count
            Integer topicsCompleted = userGrammarProgressRepository.countCompletedTopicsByUserId(userId);
            topicsCompleted = (topicsCompleted != null) ? topicsCompleted : 0;

            // Get all active topics
            List<GrammarTopicEntity> allTopics = grammarTopicRepository.findByIsActiveTrueOrderByCreatedAtAsc();

            // Build topic progress list
            List<GrammarStatsDTO.GrammarTopicProgressDTO> topicProgress = allTopics.stream()
                    .map(topic -> buildTopicProgressDTO(topic, userId))
                    .collect(Collectors.toList());

            return GrammarStatsDTO.builder()
                    .totalLessonLearned(totalLessonLearned)
                    .topicsCompleted(topicsCompleted)
                    .totalXpEarned(totalXpEarned)
                    .topicProgress(topicProgress)
                    .build();

        } catch (Exception e) {
            log.error("Error fetching grammar stats for userId: {}", userId, e);
            throw new RuntimeException("Error fetching grammar statistics", e);
        }
    }

    private GrammarStatsDTO.GrammarTopicProgressDTO buildTopicProgressDTO(GrammarTopicEntity topic, Long userId) {
        // Get total lessons in topic
        Integer totalLessons = grammarLessonRepository.countByTopicId(topic.getId());
        totalLessons = (totalLessons != null) ? totalLessons : 0;

        // Get completed lessons by user
        Integer completedLessons = userGrammarProgressRepository.countCompletedLessonsByUserIdAndTopicId(userId, topic.getId());
        completedLessons = (completedLessons != null) ? completedLessons : 0;

        // Calculate progress percentage
        Double progressPercentage = 0.0;
        if (totalLessons > 0) {
            progressPercentage = (completedLessons * 100.0) / totalLessons;
            progressPercentage = Math.round(progressPercentage * 100.0) / 100.0; // Round to 2 decimals
        }

        // Determine status
        String status;
        if (completedLessons == 0) {
            status = "new";
        } else if (completedLessons >= totalLessons && totalLessons > 0) {
            status = "completed";
        } else {
            status = "in-progress";
        }

        return GrammarStatsDTO.GrammarTopicProgressDTO.builder()
                .id(topic.getId())
                .name(topic.getName())
                .description(topic.getDescription())
                .xpReward(topic.getXpReward())
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .status(status)
                .progressPercentage(progressPercentage)
                .isActive(topic.getIsActive())
                .createdAt(topic.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<GrammarStatsDTO.GrammarTopicProgressDTO> getAllTopicsWithProgress(Long userId) {
        // Validate input
        if (userId == null || userId <= 0) {
            log.error("Invalid userId: {}", userId);
            throw new BadRequestException("User ID must be a positive number");
        }

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            log.error("User not found with id: {}", userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        try {
            log.debug("Fetching all grammar topics with progress for userId: {}", userId);

            List<GrammarTopicEntity> allTopics = grammarTopicRepository.findByIsActiveTrueOrderByCreatedAtAsc();

            return allTopics.stream()
                    .map(topic -> buildTopicProgressDTO(topic, userId))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching grammar topics with progress for userId: {}", userId, e);
            throw new RuntimeException("Error fetching grammar topics with progress", e);
        }
    }

    @Transactional(readOnly = true)
    public GrammarStatsDTO.GrammarTopicProgressDTO getTopicProgressById(Long topicId, Long userId) {
        // Validate inputs
        if (topicId == null || topicId <= 0) {
            log.error("Invalid topicId: {}", topicId);
            throw new BadRequestException("Topic ID must be a positive number");
        }
        if (userId == null || userId <= 0) {
            log.error("Invalid userId: {}", userId);
            throw new BadRequestException("User ID must be a positive number");
        }

        // Verify topic exists
        GrammarTopicEntity topic = grammarTopicRepository.findById(topicId)
                .orElseThrow(() -> {
                    log.error("Grammar topic not found with id: {}", topicId);
                    return new ResourceNotFoundException("Grammar topic not found with id: " + topicId);
                });

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            log.error("User not found with id: {}", userId);
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        try {
            log.debug("Fetching grammar topic progress for topicId: {} and userId: {}", topicId, userId);
            return buildTopicProgressDTO(topic, userId);
        } catch (Exception e) {
            log.error("Error fetching grammar topic progress for topicId: {} and userId: {}", topicId, userId, e);
            throw new RuntimeException("Error fetching grammar topic progress", e);
        }
    }
}


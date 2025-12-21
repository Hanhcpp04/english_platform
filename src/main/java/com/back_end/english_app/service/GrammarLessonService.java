package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.grammar.CompleteLessonResponseDTO;
import com.back_end.english_app.dto.respones.grammar.GrammarLessonDTO;
import com.back_end.english_app.dto.respones.grammar.GrammarLessonResponseDTO;
import com.back_end.english_app.dto.respones.grammar.LessonSummaryDTO;
import com.back_end.english_app.entity.GrammarLessonEntity;
import com.back_end.english_app.entity.GrammarTopicEntity;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.UserGrammarProgressEntity;
import com.back_end.english_app.repository.GrammarLessonRepository;
import com.back_end.english_app.repository.GrammarTopicRepository;
import com.back_end.english_app.repository.UserGrammarProgressRepository;
import com.back_end.english_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrammarLessonService {

    private final GrammarLessonRepository grammarLessonRepository;
    private final UserGrammarProgressRepository userGrammarProgressRepository;
    private final UserRepository userRepository;
    private final GrammarTopicRepository grammarTopicRepository;
    private final BadgeCheckService badgeCheckService;
    private final UserDailyStatsService userDailyStatsService;
    @Transactional(readOnly = true)
    public GrammarLessonResponseDTO getLessonsWithProgress(Long topicId, Long userId) {
        log.info("Getting lessons for topic {} and user {}", topicId, userId);

        // 1. Get all lessons for the topic
        List<GrammarLessonEntity> lessons = grammarLessonRepository.findByTopicIdAndIsActiveTrue(topicId);
        log.debug("Found {} lessons for topic {}", lessons.size(), topicId);

        // 2. Get all progress records for user and topic
        List<UserGrammarProgressEntity> progressList =
                userGrammarProgressRepository.findByUserIdAndTopicIdAndTypeTheory(userId, topicId);
        log.debug("Found {} progress records for user {}", progressList.size(), userId);

        // 3. Create a map of lesson ID to completion status
        Map<Long, Boolean> lessonProgressMap = new HashMap<>();
        for (UserGrammarProgressEntity progress : progressList) {
            Long lessonId = progress.getLesson().getId();
            Boolean isCompleted = progress.getIsCompleted();

            // If lesson already exists in map, keep true if either is true (completed takes priority)
            if (lessonProgressMap.containsKey(lessonId)) {
                lessonProgressMap.put(lessonId, lessonProgressMap.get(lessonId) || isCompleted);
            } else {
                lessonProgressMap.put(lessonId, isCompleted);
            }
        }

        // 4. Convert lessons to DTOs and add progress status
        List<GrammarLessonDTO> lessonDTOs = lessons.stream()
                .map(lesson -> {
                    Boolean isCompleted = lessonProgressMap.getOrDefault(lesson.getId(), false);

                    return GrammarLessonDTO.builder()
                            .id(lesson.getId())
                            .title(lesson.getTitle())
                            .content(lesson.getContent())
                            .xpReward(lesson.getXpReward())
                            .isActive(lesson.getIsActive())
                            .createdAt(lesson.getCreatedAt())
                            .isCompleted(isCompleted)
                            .build();
                })
                .collect(Collectors.toList());

        // 5. Calculate summary statistics
        int completedLessons = 0;
        int inProgress = 0;
        int notStarted = 0;

        for (GrammarLessonDTO lessonDTO : lessonDTOs) {
            Long lessonId = lessonDTO.getId();

            if (lessonProgressMap.containsKey(lessonId)) {
                if (lessonProgressMap.get(lessonId)) {
                    completedLessons++;
                } else {
                    inProgress++;
                }
            } else {
                notStarted++;
            }
        }

        int totalLessons = lessonDTOs.size();

        LessonSummaryDTO summary = LessonSummaryDTO.builder()
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .inProgress(inProgress)
                .notStarted(notStarted)
                .build();

        log.info("Summary - Total: {}, Completed: {}, In Progress: {}, Not Started: {}",
                totalLessons, completedLessons, inProgress, notStarted);

        // 6. Return response
        return GrammarLessonResponseDTO.builder()
                .lessons(lessonDTOs)
                .summary(summary)
                .build();
    }
    @Transactional
    public CompleteLessonResponseDTO completeLesson(Long userId, Long topicId, Long lessonId, String type) {
        log.info("Completing lesson {} for user {} with type {}", lessonId, userId, type);

        // 1. Validate and get entities
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        GrammarTopicEntity topic = grammarTopicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found with ID: " + topicId));

        GrammarLessonEntity lesson = grammarLessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + lessonId));

        // 2. Validate that lesson belongs to topic
        if (!lesson.getTopic().getId().equals(topicId)) {
            throw new RuntimeException("Lesson " + lessonId + " does not belong to topic " + topicId);
        }

        // 3. Parse and validate type
        UserGrammarProgressEntity.ProgressType progressType;
        try {
            progressType = UserGrammarProgressEntity.ProgressType.valueOf(type.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid type. Must be 'theory' or 'exercise'");
        }

        // 4. Check if progress already exists
        java.util.Optional<UserGrammarProgressEntity> existingProgress =
                userGrammarProgressRepository.findByUserIdAndLessonIdAndType(userId, lessonId, progressType);

        UserGrammarProgressEntity progress;
        String message;
        boolean shouldAwardXp = false;

        if (existingProgress.isPresent()) {
            // Update existing progress
            progress = existingProgress.get();
            if (!progress.getIsCompleted()) {
                progress.setIsCompleted(true);
                progress.setCompletedAt(java.time.LocalDateTime.now());
                shouldAwardXp = true; // Award XP when marking incomplete as complete
                message = "Lesson completed successfully";
                log.info("Updated existing progress record to completed");
            } else {
                message = "Lesson was already completed";
                log.info("Progress already marked as completed");
            }
        } else {
            // Create new progress record
            progress = new UserGrammarProgressEntity();
            progress.setUser(user);
            progress.setTopic(topic);
            progress.setLesson(lesson);
            progress.setType(progressType);
            progress.setIsCompleted(true);
            progress.setCompletedAt(java.time.LocalDateTime.now());
            shouldAwardXp = true; // Award XP for new completion
            message = "Lesson completed successfully";
            log.info("Created new progress record");
        }

        // 5. Save progress
        UserGrammarProgressEntity savedProgress = userGrammarProgressRepository.save(progress);
        log.info("Successfully saved progress for lesson {}", lessonId);

        // 6. Award XP to user if this is a new completion
        Integer xpAwarded = 0;
        if (shouldAwardXp) {
            xpAwarded = lesson.getXpReward() != null ? lesson.getXpReward() : 100;
            Integer currentXp = user.getTotalXp() != null ? user.getTotalXp() : 0;
            user.setTotalXp(currentXp + xpAwarded);
            userRepository.save(user);
            log.info("Awarded {} XP to user {}. Total XP: {} -> {}",
                    xpAwarded, userId, currentXp, user.getTotalXp());

            // Cập nhật daily stats
            userDailyStatsService.recordGrammarCompleted(user, 1);
            userDailyStatsService.recordXpEarned(user, xpAwarded);

            // 7. Kiểm tra và cập nhật huy hiệu sau khi hoàn thành bài học
            try {
                badgeCheckService.checkAndUpdateBadges(userId, "grammar");
                log.info("Badge check completed for user {} after completing grammar lesson", userId);
            } catch (Exception e) {
                log.error("Error checking badges for user {}: {}", userId, e.getMessage(), e);
                // Không throw exception để không ảnh hưởng đến flow chính
            }
        }

        // 8. Convert to DTO and return (avoiding circular reference)
        return CompleteLessonResponseDTO.builder()
                .progressId(savedProgress.getId())
                .userId(userId)
                .topicId(topicId)
                .lessonId(lessonId)
                .type(type)
                .isCompleted(savedProgress.getIsCompleted())
                .completedAt(savedProgress.getCompletedAt())
                .message(message)
                .xpAwarded(xpAwarded)
                .totalXp(user.getTotalXp())
                .build();
    }
}


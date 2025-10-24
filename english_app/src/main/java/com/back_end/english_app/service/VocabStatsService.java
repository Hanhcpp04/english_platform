package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.dashboard.TopicWithProgressDTO;
import com.back_end.english_app.dto.respones.dashboard.VocabStatsDTO;
import com.back_end.english_app.dto.respones.dashboard.VocabTopicDTO;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.exception.BadRequestException;
import com.back_end.english_app.exception.InternalServerException;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.repository.VocabUserProgressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VocabStatsService {
    VocabUserProgressRepository vocabUserProgressRepository;
    VocabTopicRepository vocabTopicRepository;

    @Transactional(readOnly = true)
    public VocabStatsDTO getUserVocabStats(Long userId) {
        // Validate input
        if (userId == null || userId <= 0) {
            log.error("Invalid userId: {}", userId);
            throw new BadRequestException("User ID must be a positive number");
        }

        try {
            log.debug("Fetching vocab stats for userId: {}", userId);

            Integer totalWordsLearned = vocabUserProgressRepository.countWordsLearnedByUserId(userId);
            Integer totalXpEarned = vocabUserProgressRepository.sumXpEarnedByUserId(userId);

            // Handle null values from repository
            totalWordsLearned = (totalWordsLearned != null) ? totalWordsLearned : 0;
            totalXpEarned = (totalXpEarned != null) ? totalXpEarned : 0;

            // Lấy số từ đã học theo từng topic
            List<Object[]> wordsPerTopic = vocabUserProgressRepository.countWordsLearnedPerTopic(userId);
            Map<Long, Integer> topicWordsMap = new HashMap<>();

            if (wordsPerTopic != null) {
                for (Object[] row : wordsPerTopic) {
                    if (row != null && row.length >= 2) {
                        topicWordsMap.put((Long) row[0], ((Number) row[1]).intValue());
                    }
                }
            }

            // Lấy tất cả topics
            List<VocabTopicEntity> allTopics = vocabTopicRepository.findAllByIsActiveTrue();
            if (allTopics == null) {
                allTopics = new ArrayList<>();
            }

            // Tạo danh sách progress cho từng topic
            List<VocabStatsDTO.TopicProgressDTO> topicProgress = new ArrayList<>();
            int completedTopics = 0;

            for (VocabTopicEntity topic : allTopics) {
                try {
                    Integer wordsLearned = topicWordsMap.getOrDefault(topic.getId(), 0);
                    Integer totalWords = (topic.getTotalWords() != null) ? topic.getTotalWords() : 0;

                    boolean isCompleted = totalWords > 0 && wordsLearned.equals(totalWords);
                    if (isCompleted) {
                        completedTopics++;
                    }

                    double completionPercentage = totalWords > 0
                            ? (wordsLearned * 100.0 / totalWords)
                            : 0.0;

                    Integer xpEarned = wordsLearned * 5;

                    VocabStatsDTO.TopicProgressDTO progress = VocabStatsDTO.TopicProgressDTO.builder()
                            .topicId(topic.getId())
                            .topicName(topic.getName())
                            .englishName(topic.getEnglishName())
                            .iconUrl(topic.getIcon_url())
                            .description(topic.getDescription())
                            .totalWords(totalWords)
                            .wordsLearned(wordsLearned)
                            .xpEarned(xpEarned)
                            .isCompleted(isCompleted)
                            .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                            .build();

                    topicProgress.add(progress);
                } catch (Exception e) {
                    log.error("Error processing topic {}: {}", topic.getId(), e.getMessage());
                    // Continue processing other topics
                }
            }

            log.debug("Successfully fetched vocab stats for userId: {}", userId);

            return VocabStatsDTO.builder()
                    .totalWordsLearned(totalWordsLearned)
                    .topicsCompleted(completedTopics)
                    .totalXpEarned(totalXpEarned)
                    .topicProgress(topicProgress)
                    .build();

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching vocab stats for userId {}: {}", userId, e.getMessage(), e);
            throw new InternalServerException("Failed to fetch vocabulary statistics");
        }
    }

    @Transactional(readOnly = true)
    public List<VocabTopicDTO> getAllTopics() {
        try {
            log.debug("Fetching all active topics");

            List<VocabTopicEntity> topics = vocabTopicRepository.findAllByIsActiveTrue();
            if (topics == null) {
                topics = new ArrayList<>();
            }

            return topics.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching all topics: {}", e.getMessage(), e);
            throw new InternalServerException("Failed to fetch vocabulary topics");
        }
    }

    @Transactional(readOnly = true)
    public VocabTopicDTO getTopicById(Long topicId) {
        // Validate input

        if (topicId == null || topicId <= 0) {
            log.error("Invalid topicId: {}", topicId);
            throw new BadRequestException("Topic ID must be a positive number");
        }

        try {
            log.debug("Fetching topic with id: {}", topicId);

            VocabTopicEntity topic = vocabTopicRepository.findById(topicId)
                    .orElseThrow(() -> {
                        log.warn("Topic not found with id: {}", topicId);
                        return new ResourceNotFoundException("Topic", "id", topicId);
                    });

            return convertToDTO(topic);

        } catch (ResourceNotFoundException | BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching topic with id {}: {}", topicId, e.getMessage(), e);
            throw new InternalServerException("Failed to fetch topic details");
        }
    }

    @Transactional(readOnly = true)
    public List<TopicWithProgressDTO> getAllTopicsWithProgress(Long userId) {
        // Validate input
        if (userId == null || userId <= 0) {
            log.error("Invalid userId: {}", userId);
            throw new BadRequestException("User ID must be a positive number");
        }

        try {
            log.debug("Fetching all topics with progress for userId: {}", userId);

            // Lấy thông tin progress của user theo topic
            List<Object[]> progressData = vocabUserProgressRepository.getTopicProgressByUserId(userId);
            Map<Long, Object[]> progressMap = new HashMap<>();

            if (progressData != null) {
                for (Object[] row : progressData) {
                    if (row != null && row.length >= 3) {
                        Long topicId = (Long) row[0];
                        progressMap.put(topicId, row);
                    }
                }
            }

            // Lấy tất cả topics
            List<VocabTopicEntity> allTopics = vocabTopicRepository.findAllByIsActiveTrue();
            if (allTopics == null) {
                allTopics = new ArrayList<>();
            }

            // Kết hợp thông tin
            List<TopicWithProgressDTO> result = allTopics.stream()
                    .map(topic -> {
                        try {
                            Integer wordsCompleted = 0;
                            Integer xpEarned = 0;

                            if (progressMap.containsKey(topic.getId())) {
                                Object[] progress = progressMap.get(topic.getId());
                                wordsCompleted = ((Number) progress[1]).intValue();
                                xpEarned = ((Number) progress[2]).intValue();
                            }

                            Integer totalWords = (topic.getTotalWords() != null) ? topic.getTotalWords() : 0;
                            boolean isCompleted = totalWords > 0 && wordsCompleted.equals(totalWords);
                            double completionPercentage = totalWords > 0
                                    ? (wordsCompleted * 100.0 / totalWords)
                                    : 0.0;

                            return TopicWithProgressDTO.builder()
                                    .id(topic.getId())
                                    .name(topic.getName())
                                    .englishName(topic.getEnglishName())
                                    .description(topic.getDescription())
                                    .iconUrl(topic.getIcon_url())
                                    .xpReward(topic.getXpReward())
                                    .totalWords(totalWords)
                                    .wordsCompleted(wordsCompleted)
                                    .xpEarned(xpEarned)
                                    .completionPercentage(Math.round(completionPercentage * 100.0) / 100.0)
                                    .isCompleted(isCompleted)
                                    .isActive(topic.getIsActive())
                                    .createdAt(topic.getCreatedAt())
                                    .updatedAt(topic.getUpdatedAt())
                                    .build();
                        } catch (Exception e) {
                            log.error("Error processing topic {}: {}", topic.getId(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(dto -> dto != null) // Remove failed conversions
                    .collect(Collectors.toList());

            log.debug("Successfully fetched {} topics with progress for userId: {}", result.size(), userId);
            return result;

        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching topics with progress for userId {}: {}", userId, e.getMessage(), e);
            throw new InternalServerException("Failed to fetch topics with progress");
        }
    }

    private VocabTopicDTO convertToDTO(VocabTopicEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("VocabTopicEntity cannot be null");
        }

        try {
            return VocabTopicDTO.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .englishName(entity.getEnglishName())
                    .description(entity.getDescription())
                    .iconUrl(entity.getIcon_url())
                    .xpReward(entity.getXpReward())
                    .totalWords(entity.getTotalWords())
                    .isActive(entity.getIsActive())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Error converting entity to DTO for topic {}: {}", entity.getId(), e.getMessage());
            throw new InternalServerException("Failed to convert topic data");
        }
    }
}

package com.back_end.english_app.dto.respones.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GrammarStatsDTO {
    @JsonProperty("totalLessonLearned")
    Integer totalLessonLearned;

    @JsonProperty("topicsCompleted")
    Integer topicsCompleted;

    @JsonProperty("totalXpEarned")
    Integer totalXpEarned;

    @JsonProperty("topicProgress")
    List<GrammarTopicProgressDTO> topicProgress;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class GrammarTopicProgressDTO {
        Long id;
        String name;
        String description;

        @JsonProperty("xp_reward")
        Integer xpReward;

        @JsonProperty("total_lessons")
        Integer totalLessons;

        @JsonProperty("completed_lessons")
        Integer completedLessons;

        String status; // "completed" | "in-progress" | "new"

        @JsonProperty("progress_percentage")
        Double progressPercentage;

        @JsonProperty("is_active")
        Boolean isActive;

        @JsonProperty("created_at")
        LocalDateTime createdAt;
    }
}


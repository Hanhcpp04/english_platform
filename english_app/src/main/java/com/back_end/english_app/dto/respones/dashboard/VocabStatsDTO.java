package com.back_end.english_app.dto.respones.dashboard;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class VocabStatsDTO {
    Integer totalWordsLearned;
    Integer topicsCompleted;
    Integer totalXpEarned;
    List<TopicProgressDTO> topicProgress;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopicProgressDTO {
        Long topicId;
        String topicName;
        Integer totalWords;
        Integer wordsLearned;
        Integer xpEarned;
        Boolean isCompleted;
        Double completionPercentage;
    }
}

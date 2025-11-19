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
public class GrammarTopicLessonsDTO {
    Long id;
    String title;
    String content;
    
    @JsonProperty("xp_reward")
    Integer xpReward;
    
    @JsonProperty("is_active")
    Boolean isActive;
    
    @JsonProperty("created_at")
    LocalDateTime createdAt;
    
    @JsonProperty("is_completed")
    Boolean isCompleted;
    
    @JsonProperty("completed_at")
    LocalDateTime completedAt;
    
    @JsonProperty("progress_percentage")
    Double progressPercentage;
    
    List<GrammarLessonProgressDTO> lessons;
}


package com.back_end.english_app.dto.respones.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabTopicResponse {
    private Long id;
    private String englishName;
    private String name;
    private String description;
    private String iconUrl;
    private Integer xpReward;
    private Integer totalWords;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

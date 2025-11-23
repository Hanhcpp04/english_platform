package com.back_end.english_app.dto.request.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabTopicUpdateRequest {
    private String englishName;
    private String name;
    private String description;
    private Integer xpReward;
    private Boolean isActive;
}

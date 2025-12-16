package com.back_end.english_app.dto.request.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabTopicRequest {
    private String englishName;
    private String name;
    private String description;
    private Integer xpReward;
}

package com.back_end.english_app.dto.request.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGrammarTopicRequest {
    private String name;
    private String description;
    private Boolean isActive;
    private Integer xpReward;
}

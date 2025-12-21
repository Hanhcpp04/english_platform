package com.back_end.english_app.dto.request.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminGrammarLessonRequest {
    private Long topicId;
    private String title;
    private String content;
    private Integer xpReward;
    private Boolean isActive;
}

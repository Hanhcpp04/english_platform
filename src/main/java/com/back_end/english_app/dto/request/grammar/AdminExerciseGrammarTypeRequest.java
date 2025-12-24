package com.back_end.english_app.dto.request.grammar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminExerciseGrammarTypeRequest {
    private Long topicId;
    private String name;
    private String description;
    private Boolean isActive = true;
}

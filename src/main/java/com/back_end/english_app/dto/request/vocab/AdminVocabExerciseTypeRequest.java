package com.back_end.english_app.dto.request.vocab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabExerciseTypeRequest {
    private String name;
    private String description;
    private String instruction;
    private Boolean isActive = true;
}

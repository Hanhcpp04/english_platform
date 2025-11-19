package com.back_end.english_app.dto.respones.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTypeStructureDTO {
    private Long id;
    private String name;
    private String description;
    private Integer totalQuestions;
}

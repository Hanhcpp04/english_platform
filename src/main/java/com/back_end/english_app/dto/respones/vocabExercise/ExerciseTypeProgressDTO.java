package com.back_end.english_app.dto.respones.vocabExercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTypeProgressDTO {
    private Integer typeId;
    private String typeName;
    private Integer total;
    private Integer completed;
    private Integer percentage;
}

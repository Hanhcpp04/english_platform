package com.back_end.english_app.dto.respones.vocabExercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseTypeDTO {
    private Integer id;
    private String name;
    private String description;
    private Integer questionCount;
    private Integer completedCount;
}

package com.back_end.english_app.dto.respones.vocabExercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverallProgressDTO {
    private Integer totalWords;
    private Integer learnedWords;
    private Integer totalExercises;
    private Integer completedExercises;
    private Integer totalXp;
    private Integer percentage;
}

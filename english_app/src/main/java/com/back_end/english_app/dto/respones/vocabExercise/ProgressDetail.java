package com.back_end.english_app.dto.respones.vocabExercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDetail {
    private Integer completedQuestions;
    private Integer totalQuestions;
    private Integer percentage;
}

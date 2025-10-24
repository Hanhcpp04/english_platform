package com.back_end.english_app.dto.respones.vocabExercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionsResponse {
    private String exerciseType;
    private List<QuestionDTO> questions;
}

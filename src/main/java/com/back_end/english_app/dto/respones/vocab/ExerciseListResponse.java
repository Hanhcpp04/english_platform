package com.back_end.english_app.dto.respones.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseListResponse {
    private Long topicId;
    private String topicName;
    private String topicNameVi;
    private Integer totalExercises;
    private Map<String, List<ExerciseQuestionResponse>> exercises;
}


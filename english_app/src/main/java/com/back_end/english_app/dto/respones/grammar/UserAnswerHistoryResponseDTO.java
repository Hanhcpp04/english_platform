package com.back_end.english_app.dto.respones.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswerHistoryResponseDTO {

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("lesson_id")
    private Long lessonId;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("total_questions")
    private Integer totalQuestions;

    @JsonProperty("correct_answers")
    private Integer correctAnswers;

    private List<UserAnswerDTO> answers;
}


package com.back_end.english_app.dto.respones.vocabExercise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO cho response lịch sử làm bài
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabAnswerHistoryResponseDTO {

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("topic_name")
    private String topicName;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("type_name")
    private String typeName;

    @JsonProperty("total_attempts")
    private Integer totalAttempts;

    @JsonProperty("correct_answers")
    private Integer correctAnswers;

    @JsonProperty("total_xp_earned")
    private Integer totalXpEarned;

    private List<VocabAnswerHistoryDTO> answers;
}


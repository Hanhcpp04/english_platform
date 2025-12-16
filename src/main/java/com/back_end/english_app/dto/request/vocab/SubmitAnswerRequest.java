package com.back_end.english_app.dto.request.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAnswerRequest {
    private Long userId;
    private Long questionId;
    private Long wordId;
    private Long topicId;
    private String userAnswer;
    private Integer timeSpent; // seconds
}


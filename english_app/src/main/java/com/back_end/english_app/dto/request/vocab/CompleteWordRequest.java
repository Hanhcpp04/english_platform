package com.back_end.english_app.dto.request.vocab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteWordRequest {
    private Long wordId;
    private Long topicId;
}


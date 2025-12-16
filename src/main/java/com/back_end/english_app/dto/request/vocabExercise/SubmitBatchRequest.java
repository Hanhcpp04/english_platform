package com.back_end.english_app.dto.request.vocabExercise;

import com.back_end.english_app.dto.respones.vocabExercise.BatchAnswerItem;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class SubmitBatchRequest {
    @NotNull
    private Integer userId;

    @NotNull
    private Integer topicId;

    @NotNull
    private List<BatchAnswerItem> answers;
}
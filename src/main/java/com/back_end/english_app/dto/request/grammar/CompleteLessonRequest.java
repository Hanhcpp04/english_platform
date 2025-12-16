package com.back_end.english_app.dto.request.grammar;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteLessonRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    @NotNull(message = "Type is required (theory or exercise)")
    private String type;
}


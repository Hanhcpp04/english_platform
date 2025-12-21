package com.back_end.english_app.dto.request.writing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitWritingRequest {

    @NotNull(message = "Task ID is required")
    private Integer taskId;

    @NotBlank(message = "Writing content cannot be empty")
    private String content;

    private String mode; // PROMPT or FREE

    private Integer timeSpent; // Time in seconds
}

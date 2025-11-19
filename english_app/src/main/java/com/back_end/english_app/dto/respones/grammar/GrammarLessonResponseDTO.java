package com.back_end.english_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarLessonResponseDTO {
    private List<GrammarLessonDTO> lessons;
    private LessonSummaryDTO summary;
}


package com.back_end.english_app.dto.respones.dashboard;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DashboardSummaryDTO {
    // Module stats
    ModuleStatsDTO vocabularyStats;
    ModuleStatsDTO grammarStats;
    ModuleStatsDTO writingStats;
    ModuleStatsDTO forumStats;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ModuleStatsDTO {
        String moduleName;
        Integer completed;
        Integer total;
        String progressText; // e.g., "45/100", "8 bài"
        Double progressPercentage;
    }
}

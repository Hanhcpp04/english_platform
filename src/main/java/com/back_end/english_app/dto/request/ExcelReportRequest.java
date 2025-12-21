package com.back_end.english_app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcelReportRequest {
    private String reportType; // "USER_ACTIVITY", "VOCABULARY", "GRAMMAR", "WRITING", "FORUM", "OVERALL"
    private LocalDate startDate;
    private LocalDate endDate;
    private String dateGrouping; // "DAY", "MONTH", "YEAR"
}

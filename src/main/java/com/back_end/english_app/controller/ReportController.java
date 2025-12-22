package com.back_end.english_app.controller;

import com.back_end.english_app.dto.request.ExcelReportRequest;
import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.service.user.EnterpriseExcelReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final EnterpriseExcelReportService excelReportService;

    @GetMapping("/export")
    public ResponseEntity<?> exportExcelReport(
            @RequestParam(defaultValue = "OVERALL") String reportType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "DAY") String dateGrouping
    ) {
        try {
            log.info("Exporting Excel report - Type: {}, Start: {}, End: {}", reportType, startDate, endDate);
            
            ExcelReportRequest request = ExcelReportRequest.builder()
                    .reportType(reportType)
                    .startDate(startDate)
                    .endDate(endDate)
                    .dateGrouping(dateGrouping)
                    .build();

            byte[] excelData = excelReportService.generateExcelReport(request);

            // Generate filename with current date
            String filename = String.format("DashboardReport_%s_%s.xlsx", 
                    reportType, 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (IOException e) {
            log.error("Error generating Excel report", e);
            return ResponseEntity.internalServerError()
                    .body(APIResponse.<String>builder()
                            .code(5000)
                            .message("Lỗi khi tạo báo cáo Excel: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error generating Excel report", e);
            return ResponseEntity.internalServerError()
                    .body(APIResponse.<String>builder()
                            .code(5000)
                            .message("Lỗi không xác định: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/types")
    public ResponseEntity<APIResponse<String[]>> getReportTypes() {
        String[] reportTypes = {
                "OVERALL",           // All sheets with complete analysis
                "USER_ACTIVITY",     // Dashboard + User Performance sheets
                "VOCABULARY",        // Retention analysis for vocabulary topics
                "WRITING"            // Writing analysis with AI scores
        };
        
        return ResponseEntity.ok(APIResponse.<String[]>builder()
                .code(1000)
                .message("Lấy danh sách loại báo cáo thành công")
                .result(reportTypes)
                .build());
    }
}

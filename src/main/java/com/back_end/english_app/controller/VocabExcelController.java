package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.response.VocabImportResultDTO;
import com.back_end.english_app.service.VocabExcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/admin-vocab/excel")
@RequiredArgsConstructor
@Slf4j
public class VocabExcelController {

    private final VocabExcelService vocabExcelService;

    /**
     * Download Excel template for vocabulary import
     * GET /admin-vocab/excel/template
     */
    @GetMapping("/template")
    public ResponseEntity<?> downloadTemplate() {
        try {
            log.info("Generating vocabulary Excel template");
            
            byte[] excelData = vocabExcelService.generateVocabTemplate();

            String filename = String.format("Mau_Nhap_Tu_Vung_%s.xlsx", 
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);

        } catch (IOException e) {
            log.error("Error generating vocabulary template", e);
            return ResponseEntity.internalServerError()
                    .body(APIResponse.<String>builder()
                            .code(5000)
                            .message("Lỗi khi tạo mẫu Excel: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Import vocabulary from Excel file
     * POST /admin-vocab/excel/import?topicId=1
     */
    @PostMapping("/import")
    public ResponseEntity<APIResponse<VocabImportResultDTO>> importVocabulary(
            @RequestParam Long topicId,
            @RequestParam("file") MultipartFile file) {
        try {
            log.info("Importing vocabulary from Excel for topic ID: {}", topicId);
            
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.<VocabImportResultDTO>builder()
                                .code(4000)
                                .message("File không được để trống")
                                .build());
            }
            
            // Validate file type
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.toLowerCase().endsWith(".xlsx")) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.<VocabImportResultDTO>builder()
                                .code(4000)
                                .message("File phải có định dạng .xlsx")
                                .build());
            }
            
            // Import
            VocabImportResultDTO result = vocabExcelService.importVocabFromExcel(file, topicId);
            
            if (result.getSuccessCount() == 0 && result.getFailedCount() > 0) {
                return ResponseEntity.ok()
                        .body(APIResponse.<VocabImportResultDTO>builder()
                                .code(4001)
                                .message("Import thất bại: Không có dòng nào được nhập thành công")
                                .result(result)
                                .build());
            }
            
            return ResponseEntity.ok()
                    .body(APIResponse.<VocabImportResultDTO>builder()
                            .code(1000)
                            .message(result.getMessage())
                            .result(result)
                            .build());

        } catch (IOException e) {
            log.error("Error importing vocabulary from Excel", e);
            return ResponseEntity.internalServerError()
                    .body(APIResponse.<VocabImportResultDTO>builder()
                            .code(5000)
                            .message("Lỗi khi đọc file Excel: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Unexpected error importing vocabulary", e);
            return ResponseEntity.internalServerError()
                    .body(APIResponse.<VocabImportResultDTO>builder()
                            .code(5000)
                            .message("Lỗi không xác định: " + e.getMessage())
                            .build());
        }
    }
}

package com.back_end.english_app.service.user;

import com.back_end.english_app.dto.request.VocabWordImportDTO;
import com.back_end.english_app.dto.respones.vocab.VocabImportResultDTO;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.entity.VocabWordEntity;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.repository.VocabWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VocabExcelService {

    private final VocabWordRepository vocabWordRepository;
    private final VocabTopicRepository vocabTopicRepository;

    /**
     * Generate Excel template for vocabulary import
     */
    public byte[] generateVocabTemplate() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        try {
            XSSFSheet sheet = workbook.createSheet("Mẫu Nhập Từ Vựng");
            
            // Styles
            CellStyle titleStyle = createTitleStyle(workbook);
            CellStyle instructionStyle = createInstructionStyle(workbook);
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle exampleStyle = createExampleStyle(workbook);
            CellStyle requiredStyle = createRequiredHeaderStyle(workbook);
            
            int rowNum = 0;
            
            // Title
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("📚 MẪU NHẬP TỪ VỰNG - ENGLISHSMART");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
            titleRow.setHeightInPoints(30);
            
            rowNum++; // Empty row
            
            // Instructions
            Row instructionRow1 = sheet.createRow(rowNum++);
            Cell instrCell1 = instructionRow1.createCell(0);
            instrCell1.setCellValue("⚠️ HƯỚNG DẪN NHẬP LIỆU:");
            instrCell1.setCellStyle(instructionStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 6));
            
            String[] instructions = {
                "1. Các cột có dấu (*) là BẮT BUỘC phải nhập",
                "2. Từ tiếng Anh: Nhập từ vựng tiếng Anh (ví dụ: hello, beautiful, run)",
                "3. Nghĩa tiếng Việt: Nhập nghĩa của từ (ví dụ: xin chào, đẹp, chạy)",
                "4. Phát âm: Nhập phiên âm IPA (không bắt buộc, ví dụ: /həˈloʊ/)",
                "5. Ví dụ: Câu ví dụ tiếng Anh sử dụng từ này",
                "6. Dịch ví dụ: Bản dịch tiếng Việt của câu ví dụ",
                "7. Loại từ: Noun, Verb, Adjective, Adverb, v.v.",
                "8. XP thưởng: Điểm kinh nghiệm (mặc định 5, có thể để trống)",
                "⚠️ KHÔNG XÓA hoặc chỉnh sửa dòng tiêu đề. Chỉ nhập dữ liệu từ dòng 10 trở đi!"
            };
            
            for (String instruction : instructions) {
                Row instrRow = sheet.createRow(rowNum++);
                Cell instrCell = instrRow.createCell(0);
                instrCell.setCellValue(instruction);
                instrCell.setCellStyle(instructionStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 0, 6));
            }
            
            rowNum++; // Empty row
            
            // Header row
            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {
                "Từ tiếng Anh (*)",
                "Nghĩa tiếng Việt (*)",
                "Phát âm (IPA)",
                "Ví dụ",
                "Dịch ví dụ",
                "Loại từ",
                "XP thưởng"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                if (headers[i].contains("(*)")) {
                    cell.setCellStyle(requiredStyle);
                } else {
                    cell.setCellStyle(headerStyle);
                }
            }
            headerRow.setHeightInPoints(25);
            
            // Example row
            Row exampleRow = sheet.createRow(rowNum++);
            String[] examples = {
                "hello",
                "xin chào",
                "/həˈloʊ/",
                "Hello, how are you?",
                "Xin chào, bạn khỏe không?",
                "Interjection",
                "5"
            };
            
            for (int i = 0; i < examples.length; i++) {
                Cell cell = exampleRow.createCell(i);
                cell.setCellValue(examples[i]);
                cell.setCellStyle(exampleStyle);
            }
            
            // Add more empty rows for data entry
            for (int i = 0; i < 50; i++) {
                sheet.createRow(rowNum++);
            }
            
            // Set column widths
            sheet.setColumnWidth(0, 6000);  // English word
            sheet.setColumnWidth(1, 8000);  // Vietnamese meaning
            sheet.setColumnWidth(2, 5000);  // Pronunciation
            sheet.setColumnWidth(3, 12000); // Example
            sheet.setColumnWidth(4, 12000); // Example translation
            sheet.setColumnWidth(5, 4500);  // Word type
            sheet.setColumnWidth(6, 3500);  // XP
            
            // Freeze panes at data start
            sheet.createFreezePane(0, rowNum - 51);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    /**
     * Import vocabulary from Excel file
     */
    public VocabImportResultDTO importVocabFromExcel(MultipartFile file, Long topicId) throws IOException {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failedCount = 0;
        int totalRows = 0;
        
        // Validate topic exists
        VocabTopicEntity topic = vocabTopicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic không tồn tại với ID: " + topicId));
        
        try (XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream())) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            // Find the header row (should contain "Từ tiếng Anh")
            int headerRowNum = findHeaderRow(sheet);
            if (headerRowNum == -1) {
                return VocabImportResultDTO.builder()
                        .totalRows(0)
                        .successCount(0)
                        .failedCount(0)
                        .errors(List.of("Không tìm thấy dòng tiêu đề. File có thể không đúng định dạng."))
                        .message("Import thất bại")
                        .build();
            }
            
            // Start reading from data row (after header)
            int startRow = headerRowNum + 2; // Skip header and example row
            
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }
                
                totalRows++;
                
                try {
                    VocabWordImportDTO wordDTO = parseRowToDTO(row, i);
                    
                    // Validate required fields
                    if (wordDTO.getEnglishWord() == null || wordDTO.getEnglishWord().trim().isEmpty()) {
                        errors.add("Dòng " + (i + 1) + ": Thiếu từ tiếng Anh (bắt buộc)");
                        failedCount++;
                        continue;
                    }
                    
                    if (wordDTO.getVietnameseMeaning() == null || wordDTO.getVietnameseMeaning().trim().isEmpty()) {
                        errors.add("Dòng " + (i + 1) + ": Thiếu nghĩa tiếng Việt (bắt buộc)");
                        failedCount++;
                        continue;
                    }
                    
                    // Create and save entity
                    VocabWordEntity wordEntity = new VocabWordEntity();
                    wordEntity.setTopic(topic);
                    wordEntity.setEnglishWord(wordDTO.getEnglishWord().trim());
                    wordEntity.setVietnameseMeaning(wordDTO.getVietnameseMeaning().trim());
                    wordEntity.setPronunciation(wordDTO.getPronunciation());
                    wordEntity.setExampleSentence(wordDTO.getExampleSentence());
                    wordEntity.setExampleTranslation(wordDTO.getExampleTranslation());
                    wordEntity.setWordType(wordDTO.getWordType());
                    wordEntity.setXpReward(wordDTO.getXpReward() != null ? wordDTO.getXpReward() : 5);
                    wordEntity.setIsActive(true);
                    
                    vocabWordRepository.save(wordEntity);
                    successCount++;
                    
                } catch (Exception e) {
                    errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                    failedCount++;
                    log.error("Error parsing row {}: {}", i + 1, e.getMessage());
                }
            }
            
            // Update topic's total words count
            topic.setTotalWords((int) vocabWordRepository.countByTopicId(topicId));
            vocabTopicRepository.save(topic);
            
        } catch (Exception e) {
            log.error("Error importing vocabulary from Excel", e);
            return VocabImportResultDTO.builder()
                    .totalRows(totalRows)
                    .successCount(successCount)
                    .failedCount(failedCount)
                    .errors(List.of("Lỗi đọc file: " + e.getMessage()))
                    .message("Import thất bại")
                    .build();
        }
        
        String message = String.format("Import hoàn tất: %d thành công, %d thất bại (Tổng: %d)", 
                                       successCount, failedCount, totalRows);
        
        return VocabImportResultDTO.builder()
                .totalRows(totalRows)
                .successCount(successCount)
                .failedCount(failedCount)
                .errors(errors)
                .message(message)
                .build();
    }

    // ==================== HELPER METHODS ====================
    
    private int findHeaderRow(XSSFSheet sheet) {
        for (int i = 0; i <= Math.min(20, sheet.getLastRowNum()); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell firstCell = row.getCell(0);
                if (firstCell != null && getCellValueAsString(firstCell).contains("Từ tiếng Anh")) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    private boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        for (int i = 0; i < 2; i++) { // Check first 2 required columns
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private VocabWordImportDTO parseRowToDTO(Row row, int rowNum) {
        return VocabWordImportDTO.builder()
                .englishWord(getCellValueAsString(row.getCell(0)))
                .vietnameseMeaning(getCellValueAsString(row.getCell(1)))
                .pronunciation(getCellValueAsString(row.getCell(2)))
                .exampleSentence(getCellValueAsString(row.getCell(3)))
                .exampleTranslation(getCellValueAsString(row.getCell(4)))
                .wordType(getCellValueAsString(row.getCell(5)))
                .xpReward(getCellValueAsInteger(row.getCell(6)))
                .build();
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return (int) cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                return value.isEmpty() ? null : Integer.parseInt(value);
            }
        } catch (Exception e) {
            log.warn("Cannot parse cell value as integer: {}", e.getMessage());
        }
        return null;
    }

    // ==================== STYLE METHODS ====================
    
    private CellStyle createTitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Calibri");
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        font.setColor(new XSSFColor(new java.awt.Color(31, 78, 120), null));
        style.setFont(font);
        
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(217, 225, 242), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderBottom(BorderStyle.MEDIUM);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }
    
    private CellStyle createInstructionStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(new java.awt.Color(89, 89, 89), null));
        style.setFont(font);
        
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(255, 250, 205), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        return style;
    }
    
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(68, 114, 196), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        
        return style;
    }
    
    private CellStyle createRequiredHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(192, 0, 0), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        
        return style;
    }
    
    private CellStyle createExampleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setItalic(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(new java.awt.Color(0, 128, 0), null));
        style.setFont(font);
        
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(234, 255, 234), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
}

package com.back_end.english_app.service.user;

import com.back_end.english_app.dto.request.ExcelReportRequest;
import com.back_end.english_app.repository.*;
import com.back_end.english_app.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelReportService {

    private final UserRepository userRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;
    private final VocabUserProgressRepository vocabUserProgressRepository;
    private final UserGrammarProgressRepository userGrammarProgressRepository;
    private final WritingPromptRepository writingPromptRepository;
    private final ForumPostRepository forumPostRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserStreakRepository userStreakRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public byte[] generateExcelReport(ExcelReportRequest request) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        try {
            // Always create README and Overview
            createReadmeSheet(workbook, request);
            createOverviewSheet(workbook, request);
            
            // Create data sheets based on report type
            switch (request.getReportType()) {
                case "USER_ACTIVITY":
                    createUsersSheet(workbook);
                    createStreaksSheet(workbook);
                    createUserDailyStatsSheet(workbook, request);
                    break;
                case "VOCABULARY":
                    createVocabProgressSheet(workbook, request);
                    break;
                case "GRAMMAR":
                    createGrammarProgressSheet(workbook, request);
                    break;
                case "WRITING":
                    createWritingSheet(workbook, request);
                    break;
                case "FORUM":
                    createForumPostsSheet(workbook, request);
                    createForumCommentsSheet(workbook, request);
                    break;
                case "OVERALL":
                default:
                    createUsersSheet(workbook);
                    createStreaksSheet(workbook);
                    createUserDailyStatsSheet(workbook, request);
                    createVocabProgressSheet(workbook, request);
                    createGrammarProgressSheet(workbook, request);
                    createWritingSheet(workbook, request);
                    createForumPostsSheet(workbook, request);
                    createForumCommentsSheet(workbook, request);
                    createBadgesSheet(workbook);
                    createBadgeProgressSheet(workbook);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } finally {
            workbook.close();
        }
    }

    // ==================== STYLE CREATION ====================
    
    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Segoe UI");
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(31, 78, 120), null));
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

    private CellStyle createDataStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        XSSFColor borderColor = new XSSFColor(new java.awt.Color(216, 223, 232), null);
        style.setTopBorderColor(borderColor.getIndex());
        style.setRightBorderColor(borderColor.getIndex());
        style.setBottomBorderColor(borderColor.getIndex());
        style.setLeftBorderColor(borderColor.getIndex());
        
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    private CellStyle createAlternateRowStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        
        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(247, 249, 252), null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        XSSFColor borderColor = new XSSFColor(new java.awt.Color(216, 223, 232), null);
        style.setTopBorderColor(borderColor.getIndex());
        style.setRightBorderColor(borderColor.getIndex());
        style.setBottomBorderColor(borderColor.getIndex());
        style.setLeftBorderColor(borderColor.getIndex());
        
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    private CellStyle createNumberStyle(XSSFWorkbook workbook, boolean alternate) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        
        if (alternate) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(247, 249, 252), null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        
        return style;
    }

    private CellStyle createDateStyle(XSSFWorkbook workbook, boolean alternate) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        
        if (alternate) {
            style.setFillForegroundColor(new XSSFColor(new java.awt.Color(247, 249, 252), null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }

    // ==================== SHEET CREATORS ====================
    
    private void createReadmeSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("README");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("key");
        headerRow.createCell(1).setCellValue("value");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        addSimpleRow(sheet, rowNum++, "report_name", "EnglishSmart Admin Dashboard Report", dataStyle);
        addSimpleRow(sheet, rowNum++, "generated_at", LocalDate.now().atStartOfDay().format(DATETIME_FORMATTER), dataStyle);
        addSimpleRow(sheet, rowNum++, "report_type", request.getReportType(), dataStyle);
        addSimpleRow(sheet, rowNum++, "date_from", request.getStartDate() != null ? request.getStartDate().toString() : "All Time", dataStyle);
        addSimpleRow(sheet, rowNum++, "date_to", request.getEndDate() != null ? request.getEndDate().toString() : LocalDate.now().toString(), dataStyle);
        addSimpleRow(sheet, rowNum++, "data_source", "EnglishSmart MySQL Database", dataStyle);
        addSimpleRow(sheet, rowNum++, "notes", "All dates in ISO format. Booleans as TRUE/FALSE", dataStyle);
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, 2);
        sheet.createFreezePane(0, 1);
    }
    
    private void createOverviewSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("Overview");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        CellStyle numberStyle = createNumberStyle(workbook, false);
        CellStyle numberAltStyle = createNumberStyle(workbook, true);
        CellStyle dateStyle = createDateStyle(workbook, false);
        
        int rowNum = 0;
        String asOfDate = LocalDate.now().format(DATE_FORMATTER);
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"metric_key", "metric_name", "value", "as_of_date", "notes"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        // Gather metrics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        long vocabCompleted = request.getStartDate() != null && request.getEndDate() != null
            ? vocabUserProgressRepository.countByCompletedAtBetween(request.getStartDate().atStartOfDay(), request.getEndDate().atTime(23, 59, 59))
            : vocabUserProgressRepository.countByIsCompleted(true);
        long grammarCompleted = request.getStartDate() != null && request.getEndDate() != null
            ? userGrammarProgressRepository.countByCompletedAtBetween(request.getStartDate().atStartOfDay(), request.getEndDate().atTime(23, 59, 59))
            : userGrammarProgressRepository.countByIsCompletedTrue();
        long writingSubmitted = request.getStartDate() != null && request.getEndDate() != null
            ? writingPromptRepository.countBySubmittedAtBetween(request.getStartDate().atStartOfDay(), request.getEndDate().atTime(23, 59, 59))
            : writingPromptRepository.countByIsCompletedTrue();
        long forumPosts = forumPostRepository.count();
        Long totalXP = userRepository.sumTotalXp();
        
        // Add metrics
        addMetricRow(sheet, rowNum++, "total_users", "Total Users", totalUsers, asOfDate, "All registered users", dataStyle, numberStyle, dateStyle);
        addMetricRow(sheet, rowNum++, "active_users", "Active Users", activeUsers, asOfDate, "Users with is_active=TRUE", alternateStyle, numberAltStyle, dateStyle);
        addMetricRow(sheet, rowNum++, "vocab_completed", "Vocabulary Completed", vocabCompleted, asOfDate, "Completed vocab items", dataStyle, numberStyle, dateStyle);
        addMetricRow(sheet, rowNum++, "grammar_completed", "Grammar Completed", grammarCompleted, asOfDate, "Completed grammar items", alternateStyle, numberAltStyle, dateStyle);
        addMetricRow(sheet, rowNum++, "writing_submitted", "Writing Submitted", writingSubmitted, asOfDate, "Submitted writing", dataStyle, numberStyle, dateStyle);
        addMetricRow(sheet, rowNum++, "forum_posts", "Forum Posts", forumPosts, asOfDate, "Total forum posts", alternateStyle, numberAltStyle, dateStyle);
        addMetricRow(sheet, rowNum++, "total_xp", "Total XP", totalXP != null ? totalXP : 0, asOfDate, "Sum of all user XP", dataStyle, numberStyle, dateStyle);
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createUsersSheet(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("Users");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        // Header
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"user_id", "username", "email", "fullname", "role", "is_active", "total_xp", "provider", "created_at", "updated_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        // Data
        List<com.back_end.english_app.entity.UserEntity> users = userRepository.findAll();
        for (com.back_end.english_app.entity.UserEntity user : users) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(user.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(user.getUsername());
            row.getCell(1).setCellStyle(rowStyle);
            
            row.createCell(2).setCellValue(user.getEmail());
            row.getCell(2).setCellStyle(rowStyle);
            
            row.createCell(3).setCellValue(user.getFullname());
            row.getCell(3).setCellStyle(rowStyle);
            
            row.createCell(4).setCellValue(user.getRole().name());
            row.getCell(4).setCellStyle(rowStyle);
            
            row.createCell(5).setCellValue(user.getIsActive() ? "TRUE" : "FALSE");
            row.getCell(5).setCellStyle(rowStyle);
            
            row.createCell(6).setCellValue(user.getTotalXp() != null ? user.getTotalXp() : 0);
            row.getCell(6).setCellStyle(rowNumberStyle);
            
            row.createCell(7).setCellValue(user.getProvider() != null ? user.getProvider() : "");
            row.getCell(7).setCellStyle(rowStyle);
            
            row.createCell(8).setCellValue(user.getCreatedAt().format(DATETIME_FORMATTER));
            row.getCell(8).setCellStyle(rowDateStyle);
            
            row.createCell(9).setCellValue(user.getUpdatedAt() != null ? user.getUpdatedAt().format(DATETIME_FORMATTER) : "");
            row.getCell(9).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createStreaksSheet(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("Streaks");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"user_id", "current_streak", "longest_streak", "last_activity_date", "streak_start_date", "longest_streak_date", "total_study_days"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.UserStreakEntity> streaks = userStreakRepository.findAll();
        for (com.back_end.english_app.entity.UserStreakEntity streak : streaks) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(streak.getUser().getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(streak.getCurrentStreak() != null ? streak.getCurrentStreak() : 0);
            row.getCell(1).setCellStyle(rowNumberStyle);
            
            row.createCell(2).setCellValue(streak.getLongestStreak() != null ? streak.getLongestStreak() : 0);
            row.getCell(2).setCellStyle(rowNumberStyle);
            
            row.createCell(3).setCellValue(streak.getLastActivityDate() != null ? streak.getLastActivityDate().format(DATE_FORMATTER) : "");
            row.getCell(3).setCellStyle(rowDateStyle);
            
            row.createCell(4).setCellValue(streak.getStreakStartDate() != null ? streak.getStreakStartDate().format(DATE_FORMATTER) : "");
            row.getCell(4).setCellStyle(rowDateStyle);
            
            row.createCell(5).setCellValue(streak.getLongestStreakDate() != null ? streak.getLongestStreakDate().format(DATE_FORMATTER) : "");
            row.getCell(5).setCellStyle(rowDateStyle);
            
            row.createCell(6).setCellValue(streak.getTotalStudyDays() != null ? streak.getTotalStudyDays() : 0);
            row.getCell(6).setCellStyle(rowNumberStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createUserDailyStatsSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("UserDailyStats");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"user_id", "date", "vocab_learned", "grammar_completed", "exercises_done", "writing_submitted", "forum_posts", "forum_comments", "study_time_minutes", "xp_earned", "accuracy_rate", "is_study_day"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<UserDailyStatsEntity> stats = userDailyStatsRepository.findAll();
        
        // Filter by date if provided
        if (request.getStartDate() != null && request.getEndDate() != null) {
            stats = stats.stream()
                .filter(s -> !s.getDate().isBefore(request.getStartDate()) && !s.getDate().isAfter(request.getEndDate()))
                .toList();
        }
        
        for (com.back_end.english_app.entity.UserDailyStatsEntity stat : stats) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(stat.getUser().getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(stat.getDate().format(DATE_FORMATTER));
            row.getCell(1).setCellStyle(rowDateStyle);
            
            row.createCell(2).setCellValue(stat.getVocabLearned() != null ? stat.getVocabLearned() : 0);
            row.getCell(2).setCellStyle(rowNumberStyle);
            
            row.createCell(3).setCellValue(stat.getGrammarCompleted() != null ? stat.getGrammarCompleted() : 0);
            row.getCell(3).setCellStyle(rowNumberStyle);
            
            row.createCell(4).setCellValue(stat.getExercisesDone() != null ? stat.getExercisesDone() : 0);
            row.getCell(4).setCellStyle(rowNumberStyle);
            
            row.createCell(5).setCellValue(stat.getWritingSubmitted() != null ? stat.getWritingSubmitted() : 0);
            row.getCell(5).setCellStyle(rowNumberStyle);
            
            row.createCell(6).setCellValue(stat.getForumPosts() != null ? stat.getForumPosts() : 0);
            row.getCell(6).setCellStyle(rowNumberStyle);
            
            row.createCell(7).setCellValue(stat.getForumComments() != null ? stat.getForumComments() : 0);
            row.getCell(7).setCellStyle(rowNumberStyle);
            
            row.createCell(8).setCellValue(stat.getStudyTimeMinutes() != null ? stat.getStudyTimeMinutes() : 0);
            row.getCell(8).setCellStyle(rowNumberStyle);
            
            row.createCell(9).setCellValue(stat.getXpEarned() != null ? stat.getXpEarned() : 0);
            row.getCell(9).setCellStyle(rowNumberStyle);
            
            row.createCell(10).setCellValue(stat.getAccuracyRate() != null ? stat.getAccuracyRate().toString() : "");
            row.getCell(10).setCellStyle(rowStyle);
            
            row.createCell(11).setCellValue(stat.getIsStudyDay() ? "TRUE" : "FALSE");
            row.getCell(11).setCellStyle(rowStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createVocabProgressSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("VocabProgress");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"id", "user_id", "topic_id", "word_id", "question_id", "type", "is_completed", "completed_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.VocabUserProgress> progressList = vocabUserProgressRepository.findAll();
        
        // Filter by date if provided
        if (request.getStartDate() != null && request.getEndDate() != null) {
            progressList = progressList.stream()
                .filter(p -> p.getCompletedAt() != null && 
                    !p.getCompletedAt().toLocalDate().isBefore(request.getStartDate()) && 
                    !p.getCompletedAt().toLocalDate().isAfter(request.getEndDate()))
                .toList();
        }
        
        for (com.back_end.english_app.entity.VocabUserProgress progress : progressList) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(progress.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(progress.getUser() != null ? progress.getUser().getId() : 0);
            row.getCell(1).setCellStyle(rowNumberStyle);
            
            row.createCell(2).setCellValue(progress.getTopic() != null ? progress.getTopic().getId() : 0);
            row.getCell(2).setCellStyle(rowNumberStyle);
            
            row.createCell(3).setCellValue(progress.getWord() != null ? progress.getWord().getId() : 0);
            row.getCell(3).setCellStyle(rowNumberStyle);
            
            row.createCell(4).setCellValue(progress.getQuestion() != null ? progress.getQuestion().getId() : 0);
            row.getCell(4).setCellStyle(rowNumberStyle);
            
            row.createCell(5).setCellValue(progress.getType().name());
            row.getCell(5).setCellStyle(rowStyle);
            
            row.createCell(6).setCellValue(progress.getIsCompleted() ? "TRUE" : "FALSE");
            row.getCell(6).setCellStyle(rowStyle);
            
            row.createCell(7).setCellValue(progress.getCompletedAt() != null ? progress.getCompletedAt().format(DATETIME_FORMATTER) : "");
            row.getCell(7).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createGrammarProgressSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("GrammarProgress");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"id", "user_id", "topic_id", "lesson_id", "question_id", "type", "is_completed", "completed_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.UserGrammarProgressEntity> progressList = userGrammarProgressRepository.findAll();
        
        // Filter by date if provided
        if (request.getStartDate() != null && request.getEndDate() != null) {
            progressList = progressList.stream()
                .filter(p -> p.getCompletedAt() != null && 
                    !p.getCompletedAt().toLocalDate().isBefore(request.getStartDate()) && 
                    !p.getCompletedAt().toLocalDate().isAfter(request.getEndDate()))
                .toList();
        }
        
        for (com.back_end.english_app.entity.UserGrammarProgressEntity progress : progressList) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(progress.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(progress.getUser() != null ? progress.getUser().getId() : 0);
            row.getCell(1).setCellStyle(rowNumberStyle);
            
            row.createCell(2).setCellValue(progress.getTopic() != null ? progress.getTopic().getId() : 0);
            row.getCell(2).setCellStyle(rowNumberStyle);
            
            row.createCell(3).setCellValue(progress.getLesson() != null ? progress.getLesson().getId() : 0);
            row.getCell(3).setCellStyle(rowNumberStyle);
            
            row.createCell(4).setCellValue(progress.getQuestion() != null ? progress.getQuestion().getId() : 0);
            row.getCell(4).setCellStyle(rowNumberStyle);
            
            row.createCell(5).setCellValue(progress.getType().name());
            row.getCell(5).setCellStyle(rowStyle);
            
            row.createCell(6).setCellValue(progress.getIsCompleted() ? "TRUE" : "FALSE");
            row.getCell(6).setCellStyle(rowStyle);
            
            row.createCell(7).setCellValue(progress.getCompletedAt() != null ? progress.getCompletedAt().format(DATETIME_FORMATTER) : "");
            row.getCell(7).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createWritingSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("Writing");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"id", "user_id", "task_id", "mode", "prompt_question", "word_count", "grammar_score", "vocabulary_score", "coherence_score", "overall_score", "xp_reward", "is_completed", "submitted_at", "created_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.WritingPromptEntity> writings = writingPromptRepository.findAll();
        
        // Filter by date if provided
        if (request.getStartDate() != null && request.getEndDate() != null) {
            writings = writings.stream()
                .filter(w -> w.getSubmittedAt() != null && 
                    !w.getSubmittedAt().toLocalDate().isBefore(request.getStartDate()) && 
                    !w.getSubmittedAt().toLocalDate().isAfter(request.getEndDate()))
                .toList();
        }
        
        for (com.back_end.english_app.entity.WritingPromptEntity writing : writings) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(writing.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(writing.getUser() != null ? writing.getUser().getId() : 0);
            row.getCell(1).setCellStyle(rowNumberStyle);
            
            row.createCell(2).setCellValue(writing.getWritingTask() != null ? writing.getWritingTask().getId() : 0);
            row.getCell(2).setCellStyle(rowNumberStyle);
            
            row.createCell(3).setCellValue(writing.getMode().name());
            row.getCell(3).setCellStyle(rowStyle);
            
            row.createCell(4).setCellValue(writing.getWritingTask() != null && writing.getWritingTask().getQuestion() != null ? writing.getWritingTask().getQuestion() : "");
            row.getCell(4).setCellStyle(rowStyle);
            
            row.createCell(5).setCellValue(writing.getWordCount() != null ? writing.getWordCount() : 0);
            row.getCell(5).setCellStyle(rowNumberStyle);
            
            row.createCell(6).setCellValue(writing.getGrammarScore() != null ? writing.getGrammarScore() : 0);
            row.getCell(6).setCellStyle(rowNumberStyle);
            
            row.createCell(7).setCellValue(writing.getVocabularyScore() != null ? writing.getVocabularyScore() : 0);
            row.getCell(7).setCellStyle(rowNumberStyle);
            
            row.createCell(8).setCellValue(writing.getCoherenceScore() != null ? writing.getCoherenceScore() : 0);
            row.getCell(8).setCellStyle(rowNumberStyle);
            
            row.createCell(9).setCellValue(writing.getOverallScore() != null ? writing.getOverallScore() : 0);
            row.getCell(9).setCellStyle(rowNumberStyle);
            
            row.createCell(10).setCellValue(writing.getXpReward() != null ? writing.getXpReward() : 0);
            row.getCell(10).setCellStyle(rowNumberStyle);
            
            row.createCell(11).setCellValue(writing.getIsCompleted() ? "TRUE" : "FALSE");
            row.getCell(11).setCellStyle(rowStyle);
            
            row.createCell(12).setCellValue(writing.getSubmittedAt() != null ? writing.getSubmittedAt().format(DATETIME_FORMATTER) : "");
            row.getCell(12).setCellStyle(rowDateStyle);
            
            row.createCell(13).setCellValue(writing.getCreatedAt().format(DATETIME_FORMATTER));
            row.getCell(13).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createForumPostsSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("ForumPosts");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"id", "user_id", "title", "likes_count", "comments_count", "xp_reward", "is_active", "created_at", "updated_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.ForumPostEntity> posts = forumPostRepository.findAll();
        
        // Filter by date if provided
        if (request.getStartDate() != null && request.getEndDate() != null) {
            posts = posts.stream()
                .filter(p -> p.getCreatedAt() != null && 
                    !p.getCreatedAt().toLocalDate().isBefore(request.getStartDate()) && 
                    !p.getCreatedAt().toLocalDate().isAfter(request.getEndDate()))
                .toList();
        }
        
        for (com.back_end.english_app.entity.ForumPostEntity post : posts) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(post.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(post.getUser() != null ? post.getUser().getId() : 0);
            row.getCell(1).setCellStyle(rowNumberStyle);
            
            row.createCell(2).setCellValue(post.getTitle());
            row.getCell(2).setCellStyle(rowStyle);
            
            row.createCell(3).setCellValue(post.getLikesCount() != null ? post.getLikesCount() : 0);
            row.getCell(3).setCellStyle(rowNumberStyle);
            
            row.createCell(4).setCellValue(post.getCommentsCount() != null ? post.getCommentsCount() : 0);
            row.getCell(4).setCellStyle(rowNumberStyle);
            
            row.createCell(5).setCellValue(post.getXpReward() != null ? post.getXpReward() : 0);
            row.getCell(5).setCellStyle(rowNumberStyle);
            
            row.createCell(6).setCellValue(post.getIsActive() ? "TRUE" : "FALSE");
            row.getCell(6).setCellStyle(rowStyle);
            
            row.createCell(7).setCellValue(post.getCreatedAt().format(DATETIME_FORMATTER));
            row.getCell(7).setCellStyle(rowDateStyle);
            
            row.createCell(8).setCellValue(post.getUpdatedAt().format(DATETIME_FORMATTER));
            row.getCell(8).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createForumCommentsSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("ForumComments");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"id", "post_id", "user_id", "parent_id", "likes_count", "is_active", "created_at", "updated_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.ForumCommentEntity> comments = forumPostRepository.findAll().stream()
            .flatMap(post -> post.getComments().stream())
            .toList();
        
        for (com.back_end.english_app.entity.ForumCommentEntity comment : comments) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(comment.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(comment.getPost() != null ? comment.getPost().getId() : 0);
            row.getCell(1).setCellStyle(rowNumberStyle);
            
            row.createCell(2).setCellValue(comment.getUser() != null ? comment.getUser().getId() : 0);
            row.getCell(2).setCellStyle(rowNumberStyle);
            
            row.createCell(3).setCellValue(comment.getParent() != null ? comment.getParent().getId() : 0);
            row.getCell(3).setCellStyle(rowNumberStyle);
            
            row.createCell(4).setCellValue(comment.getLikesCount() != null ? comment.getLikesCount() : 0);
            row.getCell(4).setCellStyle(rowNumberStyle);
            
            row.createCell(5).setCellValue(comment.getIsActive() ? "TRUE" : "FALSE");
            row.getCell(5).setCellStyle(rowStyle);
            
            row.createCell(6).setCellValue(comment.getCreatedAt().format(DATETIME_FORMATTER));
            row.getCell(6).setCellStyle(rowDateStyle);
            
            row.createCell(7).setCellValue(comment.getUpdatedAt().format(DATETIME_FORMATTER));
            row.getCell(7).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createBadgesSheet(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("Badges");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"id", "name", "condition_type", "condition_value", "xp_reward", "is_active", "created_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.BadgeEntity> badges = userBadgeRepository.findAll().stream()
            .map(com.back_end.english_app.entity.UserBadgeEntity::getBadge)
            .distinct()
            .toList();
        
        for (com.back_end.english_app.entity.BadgeEntity badge : badges) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(badge.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(badge.getName());
            row.getCell(1).setCellStyle(rowStyle);
            
            row.createCell(2).setCellValue(badge.getConditionType().name());
            row.getCell(2).setCellStyle(rowStyle);
            
            row.createCell(3).setCellValue(badge.getConditionValue() != null ? badge.getConditionValue() : 0);
            row.getCell(3).setCellStyle(rowNumberStyle);
            
            row.createCell(4).setCellValue(badge.getXpReward() != null ? badge.getXpReward() : 0);
            row.getCell(4).setCellStyle(rowNumberStyle);
            
            row.createCell(5).setCellValue(badge.getIsActive() ? "TRUE" : "FALSE");
            row.getCell(5).setCellStyle(rowStyle);
            
            row.createCell(6).setCellValue(badge.getCreatedAt().format(DATETIME_FORMATTER));
            row.getCell(6).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    private void createBadgeProgressSheet(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("BadgeProgress");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle alternateStyle = createAlternateRowStyle(workbook);
        
        int rowNum = 0;
        
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"id", "user_id", "badge_id", "current_value", "target_value", "progress_percentage", "last_updated", "earned_at"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        List<com.back_end.english_app.entity.UserBadgeEntity> userBadges = userBadgeRepository.findAll();
        
        for (com.back_end.english_app.entity.UserBadgeEntity userBadge : userBadges) {
            Row row = sheet.createRow(rowNum);
            boolean isAlternate = (rowNum - 1) % 2 == 1;
            CellStyle rowStyle = isAlternate ? alternateStyle : dataStyle;
            CellStyle rowNumberStyle = createNumberStyle(workbook, isAlternate);
            CellStyle rowDateStyle = createDateStyle(workbook, isAlternate);
            
            row.createCell(0).setCellValue(userBadge.getId());
            row.getCell(0).setCellStyle(rowNumberStyle);
            
            row.createCell(1).setCellValue(userBadge.getUser() != null ? userBadge.getUser().getId() : 0);
            row.getCell(1).setCellStyle(rowNumberStyle);
            
            row.createCell(2).setCellValue(userBadge.getBadge() != null ? userBadge.getBadge().getId() : 0);
            row.getCell(2).setCellStyle(rowNumberStyle);
            
            row.createCell(3).setCellValue(userBadge.getBadge().getConditionValue());
            row.getCell(3).setCellStyle(rowNumberStyle);
            
            row.createCell(4).setCellValue(userBadge.getBadge().getConditionValue());
            row.getCell(4).setCellStyle(rowNumberStyle);
            
            row.createCell(5).setCellValue("100.00");
            row.getCell(5).setCellStyle(rowStyle);
            
            row.createCell(6).setCellValue(userBadge.getEarnedAt().format(DATETIME_FORMATTER));
            row.getCell(6).setCellStyle(rowDateStyle);
            
            row.createCell(7).setCellValue(userBadge.getEarnedAt().format(DATETIME_FORMATTER));
            row.getCell(7).setCellStyle(rowDateStyle);
            
            rowNum++;
        }
        
        // Set header row height
        sheet.getRow(0).setHeightInPoints(25);
        
        autoSizeColumns(sheet, headers.length);
        sheet.createFreezePane(0, 1);
        if (rowNum > 1) {
            sheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    // ==================== HELPER METHODS ====================
    
    private void addSimpleRow(XSSFSheet sheet, int rowNum, String key, String value, CellStyle style) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(value);
        row.getCell(0).setCellStyle(style);
        row.getCell(1).setCellStyle(style);
    }
    
    private void addMetricRow(XSSFSheet sheet, int rowNum, String key, String name, long value, String date, String notes,
                              CellStyle textStyle, CellStyle numberStyle, CellStyle dateStyle) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(key);
        row.createCell(1).setCellValue(name);
        row.createCell(2).setCellValue(value);
        row.createCell(3).setCellValue(date);
        row.createCell(4).setCellValue(notes);
        
        row.getCell(0).setCellStyle(textStyle);
        row.getCell(1).setCellStyle(textStyle);
        row.getCell(2).setCellStyle(numberStyle);
        row.getCell(3).setCellStyle(dateStyle);
        row.getCell(4).setCellStyle(textStyle);
    }
    
    private void autoSizeColumns(XSSFSheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            // Add 10% padding to auto-sized width
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, (int) (currentWidth * 1.1));
        }
    }
}

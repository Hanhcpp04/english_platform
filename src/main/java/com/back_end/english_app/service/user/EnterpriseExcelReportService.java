package com.back_end.english_app.service.user;

import com.back_end.english_app.dto.request.ExcelReportRequest;
import com.back_end.english_app.dto.response.report.*;
import com.back_end.english_app.repository.*;
import com.back_end.english_app.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enterprise-grade Excel Report Service
 * Generates comprehensive reports with professional styling and charts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnterpriseExcelReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final UserStreakRepository userStreakRepository;
    private final UserBadgeRepository userBadgeRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Main method to generate comprehensive Excel report
     */
    public byte[] generateExcelReport(ExcelReportRequest request) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        try {
            log.info("Starting Excel report generation: {}", request.getReportType());
            
            // Calculate date range
            LocalDateTime startDateTime = request.getStartDate() != null 
                ? request.getStartDate().atStartOfDay() 
                : null;
            LocalDateTime endDateTime = request.getEndDate() != null 
                ? request.getEndDate().atTime(23, 59, 59) 
                : LocalDateTime.now();
            
            // Create sheets based on report type
            switch (request.getReportType().toUpperCase()) {
                case "OVERALL":
                    createAllSheets(workbook, startDateTime, endDateTime);
                    break;
                case "USER_ACTIVITY":
                    createDashboardSheet(workbook, startDateTime, endDateTime);
                    createUserPerformanceSheet(workbook, startDateTime, endDateTime);
                    break;
                case "VOCABULARY":
                    createRetentionAnalysisSheet(workbook);
                    break;
                case "WRITING":
                    createWritingAnalysisSheet(workbook, startDateTime, endDateTime);
                    break;
                default:
                    createAllSheets(workbook, startDateTime, endDateTime);
            }
            
            // Create README sheet at the end
            createReadmeSheet(workbook, request);
            
            // Write to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            
            log.info("Excel report generated successfully");
            return outputStream.toByteArray();
            
        } finally {
            workbook.close();
        }
    }

    /**
     * Create all sheets for OVERALL report
     */
    private void createAllSheets(XSSFWorkbook workbook, LocalDateTime startDate, LocalDateTime endDate) {
        createDashboardSheet(workbook, startDate, endDate);
        createUserPerformanceSheet(workbook, startDate, endDate);
        createWritingAnalysisSheet(workbook, startDate, endDate);
        createRetentionAnalysisSheet(workbook);
    }

    // ==================== SHEET 1: SYSTEM DASHBOARD ====================
    
    /**
     * Sheet 1: System Dashboard with metrics and charts
     */
    private void createDashboardSheet(XSSFWorkbook workbook, LocalDateTime startDate, LocalDateTime endDate) {
        XSSFSheet sheet = workbook.createSheet("📊 Tổng Quan");
        ExcelStyleUtil.configureSheet(sheet);
        
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Bảng Điều Khiển Hệ Thống - Chỉ Số Tổng Quan");
        titleCell.setCellStyle(ExcelStyleUtil.createTitleStyle(workbook));
        titleRow.setHeightInPoints(30);
        rowNum++; // Empty row
        
        // Subtitle with date range
        Row subtitleRow = sheet.createRow(rowNum++);
        Cell subtitleCell = subtitleRow.createCell(0);
        String dateRange = String.format("Thời gian: %s đến %s", 
            startDate != null ? startDate.format(DATE_FORMATTER) : "Tất cả",
            endDate.format(DATE_FORMATTER));
        subtitleCell.setCellValue(dateRange);
        subtitleCell.setCellStyle(ExcelStyleUtil.createSubtitleStyle(workbook));
        rowNum++; // Empty row
        
        // Key Metrics Header
        CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Chỉ số", "Giá trị", "Trạng thái", "Thay đổi"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        ExcelStyleUtil.setHeaderHeight(sheet, rowNum - 1, 30);
        
        // Fetch metrics
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByIsActive(true);
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        Long inactiveUsers = reportRepository.countInactiveUsers(thirtyDaysAgo);
        double churnRate = totalUsers > 0 ? (inactiveUsers * 100.0 / totalUsers) : 0;
        Double avgStudyTime = reportRepository.getAverageStudyTime(
            startDate != null ? startDate.toLocalDate() : null,
            endDate != null ? endDate.toLocalDate() : null
        );
        Long totalXp = userRepository.sumTotalXp();
        long totalBadges = userBadgeRepository.count();
        
        // Add metrics rows
        rowNum = addMetricRow(sheet, workbook, rowNum, "Tổng số người dùng", totalUsers, "ACTIVE");
        rowNum = addMetricRow(sheet, workbook, rowNum, "Người dùng hoạt động (30 ngày)", activeUsers, "SUCCESS");
        rowNum = addMetricRow(sheet, workbook, rowNum, "Người dùng không hoạt động", inactiveUsers, "WARNING");
        rowNum = addMetricRowDecimal(sheet, workbook, rowNum, "Tỷ lệ rời bỏ", churnRate, "%", "WARNING");
        rowNum = addMetricRowDecimal(sheet, workbook, rowNum, "Thời gian học TB (phút/ngày)", 
            avgStudyTime != null ? avgStudyTime : 0, "", "SUCCESS");
        rowNum = addMetricRow(sheet, workbook, rowNum, "Tổng XP toàn hệ thống", totalXp != null ? totalXp : 0, "SUCCESS");
        rowNum = addMetricRow(sheet, workbook, rowNum, "Tổng huy hiệu đạt được", totalBadges, "SUCCESS");
        
        rowNum += 2; // Empty rows
        
        // Level Distribution Section
        Row levelHeaderRow = sheet.createRow(rowNum++);
        Cell levelHeaderCell = levelHeaderRow.createCell(0);
        levelHeaderCell.setCellValue("Phân Bổ Người Dùng Theo Cấp Độ");
        levelHeaderCell.setCellStyle(ExcelStyleUtil.createTitleStyle(workbook));
        rowNum++;
        
        // Level distribution table
        Row levelTableHeader = sheet.createRow(rowNum++);
        String[] levelHeaders = {"Cấp độ", "Tên cấp độ", "Số người dùng", "Tỷ lệ %"};
        for (int i = 0; i < levelHeaders.length; i++) {
            Cell cell = levelTableHeader.createCell(i);
            cell.setCellValue(levelHeaders[i]);
            cell.setCellStyle(headerStyle);
        }
        
        List<Object[]> levelDistribution = reportRepository.getLevelDistribution();
        for (int i = 0; i < levelDistribution.size(); i++) {
            Object[] data = levelDistribution.get(i);
            Row dataRow = sheet.createRow(rowNum++);
            boolean isAlternate = i % 2 == 1;
            
            // Level Number
            Cell cell0 = dataRow.createCell(0);
            cell0.setCellValue(data[0] != null ? ((Number) data[0]).intValue() : 0);
            cell0.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Level Name
            Cell cell1 = dataRow.createCell(1);
            cell1.setCellValue(data[1] != null ? data[1].toString() : "Unknown");
            cell1.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
            
            // User Count
            Cell cell2 = dataRow.createCell(2);
            long userCount = data[2] != null ? ((Number) data[2]).longValue() : 0;
            cell2.setCellValue(userCount);
            cell2.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Percentage
            Cell cell3 = dataRow.createCell(3);
            double percentage = totalUsers > 0 ? (userCount * 100.0 / totalUsers) : 0;
            cell3.setCellValue(percentage / 100.0); // For percentage format
            cell3.setCellStyle(ExcelStyleUtil.createPercentageStyle(workbook, isAlternate));
        }
        
        // Create Pie Chart for Level Distribution
        createLevelDistributionPieChart(sheet, rowNum - levelDistribution.size(), levelDistribution.size(), rowNum + 2);
        
        // Auto-size columns
        ExcelStyleUtil.autoSizeColumns(sheet, 4);
    }
    
    /**
     * Create pie chart for level distribution
     */
    private void createLevelDistributionPieChart(XSSFSheet sheet, int dataStartRow, int dataRowCount, int chartStartRow) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, chartStartRow, 13, chartStartRow + 15);
        
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Phân Bổ Người Dùng Theo Cấp Độ");
        chart.setTitleOverlay(false);
        
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.RIGHT);
        
        // Data source
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(dataStartRow, dataStartRow + dataRowCount - 1, 1, 1));
        XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(dataStartRow, dataStartRow + dataRowCount - 1, 2, 2));
        
        // Create pie chart
        XDDFPieChartData data = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
        data.setVaryColors(true);
        XDDFPieChartData.Series series = (XDDFPieChartData.Series) data.addSeries(categories, values);
        series.setTitle("Users", null);
        
        chart.plot(data);
    }

    // ==================== SHEET 2: USER PERFORMANCE ====================
    
    /**
     * Sheet 2: User Performance with detailed metrics
     */
    private void createUserPerformanceSheet(XSSFWorkbook workbook, LocalDateTime startDate, LocalDateTime endDate) {
        XSSFSheet sheet = workbook.createSheet("👥 Hiệu Suất Người Dùng");
        ExcelStyleUtil.configureSheet(sheet);
        
        int rowNum = 0;
        
        // Title
        ExcelStyleUtil.createMergedCell(sheet, rowNum++, 0, 9, 
            "Phân Tích Hiệu Suất Người Dùng", ExcelStyleUtil.createTitleStyle(workbook));
        sheet.getRow(0).setHeightInPoints(30);
        rowNum++; // Empty row
        
        // Headers
        CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {
            "ID", "Tên đăng nhập", "Email", "Cấp độ", "Tổng XP",
            "Chuỗi hiện tại", "Chuỗi dài nhất", "Tổng ngày học",
            "Số huy hiệu", "Trạng thái"
        };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        ExcelStyleUtil.setHeaderHeight(sheet, rowNum - 1, 30);
        
        // Fetch data
        List<UserPerformanceDTO> users = reportRepository.getUserPerformanceReport(startDate, endDate);
        
        // Add data rows
        for (int i = 0; i < users.size(); i++) {
            UserPerformanceDTO user = users.get(i);
            Row dataRow = sheet.createRow(rowNum++);
            boolean isAlternate = i % 2 == 1;
            
            int colNum = 0;
            
            // User ID
            Cell cell0 = dataRow.createCell(colNum++);
            cell0.setCellValue(user.getUserId());
            cell0.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Username
            Cell cell1 = dataRow.createCell(colNum++);
            cell1.setCellValue(user.getUsername());
            cell1.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
            
            // Email
            Cell cell2 = dataRow.createCell(colNum++);
            cell2.setCellValue(user.getEmail());
            cell2.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
            
            // Level
            Cell cell3 = dataRow.createCell(colNum++);
            cell3.setCellValue(user.getLevelName() != null ? user.getLevelName() : "N/A");
            cell3.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
            
            // Total XP
            Cell cell4 = dataRow.createCell(colNum++);
            cell4.setCellValue(user.getTotalXp() != null ? user.getTotalXp() : 0);
            cell4.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Current Streak
            Cell cell5 = dataRow.createCell(colNum++);
            cell5.setCellValue(user.getCurrentStreak() != null ? user.getCurrentStreak() : 0);
            cell5.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Longest Streak
            Cell cell6 = dataRow.createCell(colNum++);
            cell6.setCellValue(user.getLongestStreak() != null ? user.getLongestStreak() : 0);
            cell6.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Total Study Days
            Cell cell7 = dataRow.createCell(colNum++);
            cell7.setCellValue(user.getTotalStudyDays() != null ? user.getTotalStudyDays() : 0);
            cell7.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Badge Count
            Cell cell8 = dataRow.createCell(colNum++);
            cell8.setCellValue(user.getBadgeCount() != null ? user.getBadgeCount() : 0);
            cell8.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Status
            Cell cell9 = dataRow.createCell(colNum++);
            String status = user.getIsActive() ? "ACTIVE" : "INACTIVE";
            cell9.setCellValue(status);
            cell9.setCellStyle(ExcelStyleUtil.createBadgeStyle(workbook, status));
        }
        
        // Auto-size columns
        ExcelStyleUtil.autoSizeColumns(sheet, headers.length);
        
        // Add filter
        if (rowNum > 3) {
            sheet.setAutoFilter(new CellRangeAddress(2, rowNum - 1, 0, headers.length - 1));
        }
    }

    // ==================== SHEET 3: WRITING SKILL ANALYSIS ====================
    
    /**
     * Sheet 3: Writing Analysis with AI scores and chart
     */
    private void createWritingAnalysisSheet(XSSFWorkbook workbook, LocalDateTime startDate, LocalDateTime endDate) {
        XSSFSheet sheet = workbook.createSheet("✍️ Phân Tích Viết");
        ExcelStyleUtil.configureSheet(sheet);
        
        int rowNum = 0;
        
        // Title
        ExcelStyleUtil.createMergedCell(sheet, rowNum++, 0, 8, 
            "Phân Tích Kỹ Năng Viết - Chấm Điểm AI", ExcelStyleUtil.createTitleStyle(workbook));
        sheet.getRow(0).setHeightInPoints(30);
        rowNum++; // Empty row
        
        // Headers
        CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {
            "ID", "Người dùng", "Chế độ", "Số từ", "Thời gian (phút)",
            "Ngữ pháp", "Từ vựng", "Mạch lạc", "Tổng thể"
        };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        ExcelStyleUtil.setHeaderHeight(sheet, rowNum - 1, 30);
        
        int dataStartRow = rowNum;
        
        // Fetch data
        List<WritingAnalysisDTO> writings = reportRepository.getWritingAnalysisReport(startDate, endDate);
        
        // Add data rows
        for (int i = 0; i < writings.size(); i++) {
            WritingAnalysisDTO writing = writings.get(i);
            Row dataRow = sheet.createRow(rowNum++);
            boolean isAlternate = i % 2 == 1;
            
            int colNum = 0;
            
            // ID
            Cell cell0 = dataRow.createCell(colNum++);
            cell0.setCellValue(writing.getWritingId());
            cell0.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Username
            Cell cell1 = dataRow.createCell(colNum++);
            cell1.setCellValue(writing.getUsername());
            cell1.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
            
            // Mode
            Cell cell2 = dataRow.createCell(colNum++);
            cell2.setCellValue(writing.getMode());
            cell2.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
            
            // Word Count
            Cell cell3 = dataRow.createCell(colNum++);
            cell3.setCellValue(writing.getWordCount() != null ? writing.getWordCount() : 0);
            cell3.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Time Spent
            Cell cell4 = dataRow.createCell(colNum++);
            cell4.setCellValue(writing.getTimeSpent() != null ? writing.getTimeSpent() : 0);
            cell4.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Grammar Score
            Cell cell5 = dataRow.createCell(colNum++);
            cell5.setCellValue(writing.getGrammarScore() != null ? writing.getGrammarScore() : 0);
            cell5.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Vocabulary Score
            Cell cell6 = dataRow.createCell(colNum++);
            cell6.setCellValue(writing.getVocabularyScore() != null ? writing.getVocabularyScore() : 0);
            cell6.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Coherence Score
            Cell cell7 = dataRow.createCell(colNum++);
            cell7.setCellValue(writing.getCoherenceScore() != null ? writing.getCoherenceScore() : 0);
            cell7.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Overall Score
            Cell cell8 = dataRow.createCell(colNum++);
            cell8.setCellValue(writing.getOverallScore() != null ? writing.getOverallScore() : 0);
            cell8.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
        }
        
        // Create Stacked Bar Chart for Writing Scores
        if (writings.size() > 0) {
            createWritingScoresStackedBarChart(sheet, dataStartRow, Math.min(writings.size(), 20), rowNum + 2);
        }
        
        // Auto-size columns
        ExcelStyleUtil.autoSizeColumns(sheet, headers.length);
        
        // Add filter
        if (rowNum > 3) {
            sheet.setAutoFilter(new CellRangeAddress(2, rowNum - 1, 0, headers.length - 1));
        }
    }
    
    /**
     * Create stacked bar chart for writing scores
     */
    private void createWritingScoresStackedBarChart(XSSFSheet sheet, int dataStartRow, int dataRowCount, int chartStartRow) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, chartStartRow, 9, chartStartRow + 20);
        
        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText("Phân Bổ Điểm Viết (Top 20)");
        chart.setTitleOverlay(false);
        
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(LegendPosition.BOTTOM);
        
        // Create category axis (usernames)
        XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
        bottomAxis.setTitle("Bài nộp");
        
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        leftAxis.setTitle("Điểm số");
        leftAxis.setMinimum(0);
        leftAxis.setMaximum(100);
        
        // Data sources
        XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(sheet,
                new CellRangeAddress(dataStartRow, dataStartRow + dataRowCount - 1, 1, 1));
        
        XDDFNumericalDataSource<Double> grammarScores = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(dataStartRow, dataStartRow + dataRowCount - 1, 5, 5));
        
        XDDFNumericalDataSource<Double> vocabScores = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(dataStartRow, dataStartRow + dataRowCount - 1, 6, 6));
        
        XDDFNumericalDataSource<Double> coherenceScores = XDDFDataSourcesFactory.fromNumericCellRange(sheet,
                new CellRangeAddress(dataStartRow, dataStartRow + dataRowCount - 1, 7, 7));
        
        // Create stacked bar chart data
        XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
        data.setBarDirection(BarDirection.COL); // Vertical bars
        data.setBarGrouping(BarGrouping.STACKED);
        
        XDDFBarChartData.Series series1 = (XDDFBarChartData.Series) data.addSeries(categories, grammarScores);
        series1.setTitle("Ngữ pháp", null);
        
        XDDFBarChartData.Series series2 = (XDDFBarChartData.Series) data.addSeries(categories, vocabScores);
        series2.setTitle("Từ vựng", null);
        
        XDDFBarChartData.Series series3 = (XDDFBarChartData.Series) data.addSeries(categories, coherenceScores);
        series3.setTitle("Mạch lạc", null);
        
        chart.plot(data);
    }

    // ==================== SHEET 4: RETENTION & DROP-OFF ====================
    
    /**
     * Sheet 4: Retention Analysis (Funnel Analysis for Vocabulary Topics)
     */
    private void createRetentionAnalysisSheet(XSSFWorkbook workbook) {
        XSSFSheet sheet = workbook.createSheet("📉 Phân Tích Duy Trì");
        ExcelStyleUtil.configureSheet(sheet);
        
        int rowNum = 0;
        
        // Title
        ExcelStyleUtil.createMergedCell(sheet, rowNum++, 0, 6, 
            "Phân Tích Duy Trì & Rời Bỏ - Chủ Đề Từ Vựng", ExcelStyleUtil.createTitleStyle(workbook));
        sheet.getRow(0).setHeightInPoints(30);
        rowNum++; // Empty row
        
        // Headers
        CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {
            "Tên chủ đề", "Tổng số từ", "ND bắt đầu", "ND hoàn thành",
            "Tỷ lệ hoàn thành", "Tỷ lệ rời bỏ", "TB từ hoàn thành"
        };
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        ExcelStyleUtil.setHeaderHeight(sheet, rowNum - 1, 30);
        
        // Fetch data
        List<Object[]> retentionData = reportRepository.getRetentionAnalysisRaw();
        
        // Add data rows
        for (int i = 0; i < retentionData.size(); i++) {
            Object[] data = retentionData.get(i);
            Row dataRow = sheet.createRow(rowNum++);
            boolean isAlternate = i % 2 == 1;
            
            int colNum = 0;
            
            // Topic Name
            Cell cell0 = dataRow.createCell(colNum++);
            cell0.setCellValue(data[1] != null ? data[1].toString() : "Unknown");
            cell0.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
            
            // Total Words
            Cell cell1 = dataRow.createCell(colNum++);
            cell1.setCellValue(data[2] != null ? ((Number) data[2]).intValue() : 0);
            cell1.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Users Started
            Cell cell2 = dataRow.createCell(colNum++);
            cell2.setCellValue(data[3] != null ? ((Number) data[3]).longValue() : 0);
            cell2.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Users Completed
            Cell cell3 = dataRow.createCell(colNum++);
            cell3.setCellValue(data[4] != null ? ((Number) data[4]).longValue() : 0);
            cell3.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
            
            // Completion Rate
            Cell cell4 = dataRow.createCell(colNum++);
            double completionRate = data[5] != null ? ((Number) data[5]).doubleValue() : 0;
            cell4.setCellValue(completionRate / 100.0); // For percentage format
            cell4.setCellStyle(ExcelStyleUtil.createPercentageStyle(workbook, isAlternate));
            
            // Drop-off Rate
            Cell cell5 = dataRow.createCell(colNum++);
            double dropOffRate = data[6] != null ? ((Number) data[6]).doubleValue() : 0;
            cell5.setCellValue(dropOffRate / 100.0); // For percentage format
            cell5.setCellStyle(ExcelStyleUtil.createPercentageStyle(workbook, isAlternate));
            
            // Avg Words Completed
            Cell cell6 = dataRow.createCell(colNum++);
            cell6.setCellValue(data[7] != null ? ((Number) data[7]).doubleValue() : 0);
            cell6.setCellStyle(ExcelStyleUtil.createDecimalStyle(workbook, isAlternate));
        }
        
        // Auto-size columns
        ExcelStyleUtil.autoSizeColumns(sheet, headers.length);
        
        // Add filter
        if (rowNum > 3) {
            sheet.setAutoFilter(new CellRangeAddress(2, rowNum - 1, 0, headers.length - 1));
        }
    }

    // ==================== README SHEET ====================
    
    /**
     * Create README sheet with report metadata
     */
    private void createReadmeSheet(XSSFWorkbook workbook, ExcelReportRequest request) {
        XSSFSheet sheet = workbook.createSheet("📖 HƯỚNG DẪN");
        ExcelStyleUtil.configureSheet(sheet);
        
        int rowNum = 0;
        
        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Báo Cáo Bảng Điều Khiển Admin EnglishSmart");
        titleCell.setCellStyle(ExcelStyleUtil.createTitleStyle(workbook));
        titleRow.setHeightInPoints(30);
        rowNum += 2;
        
        // Report Info
        CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook);
        CellStyle dataStyle = ExcelStyleUtil.createDataStyle(workbook, false);
        CellStyle altStyle = ExcelStyleUtil.createDataStyle(workbook, true);
        
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Thuộc tính");
        headerRow.createCell(1).setCellValue("Giá trị");
        headerRow.getCell(0).setCellStyle(headerStyle);
        headerRow.getCell(1).setCellStyle(headerStyle);
        
        addInfoRow(sheet, rowNum++, "Loại báo cáo", request.getReportType(), dataStyle);
        addInfoRow(sheet, rowNum++, "Tạo lúc", LocalDateTime.now().format(DATETIME_FORMATTER), altStyle);
        addInfoRow(sheet, rowNum++, "Từ ngày", 
            request.getStartDate() != null ? request.getStartDate().toString() : "Tất cả", dataStyle);
        addInfoRow(sheet, rowNum++, "Đến ngày", 
            request.getEndDate() != null ? request.getEndDate().toString() : LocalDate.now().toString(), altStyle);
        addInfoRow(sheet, rowNum++, "Nguồn dữ liệu", "CSDL MySQL EnglishSmart", dataStyle);
        addInfoRow(sheet, rowNum++, "Phiên bản", "2.0 - Phiên bản Doanh nghiệp", altStyle);
        
        rowNum += 2;
        
        // Sheet Descriptions
        Row sheetHeaderRow = sheet.createRow(rowNum++);
        sheetHeaderRow.createCell(0).setCellValue("Tên trang");
        sheetHeaderRow.createCell(1).setCellValue("Mô tả");
        sheetHeaderRow.getCell(0).setCellStyle(headerStyle);
        sheetHeaderRow.getCell(1).setCellStyle(headerStyle);
        
        addInfoRow(sheet, rowNum++, "Tổng Quan", "Tổng quan hệ thống với các chỉ số và biểu đồ", dataStyle);
        addInfoRow(sheet, rowNum++, "Hiệu Suất Người Dùng", "Thống kê và hoạt động chi tiết người dùng", altStyle);
        addInfoRow(sheet, rowNum++, "Phân Tích Viết", "Bài viết được chấm điểm AI với biểu đồ", dataStyle);
        addInfoRow(sheet, rowNum++, "Phân Tích Duy Trì", "Phân tích phễu cho các chủ đề từ vựng", altStyle);
        
        rowNum += 2;
        
        // Notes
        Row notesHeaderRow = sheet.createRow(rowNum++);
        Cell notesHeaderCell = notesHeaderRow.createCell(0);
        notesHeaderCell.setCellValue("Lưu ý quan trọng:");
        notesHeaderCell.setCellStyle(ExcelStyleUtil.createTitleStyle(workbook));
        
        CellStyle noteStyle = ExcelStyleUtil.createDataStyle(workbook, false);
        addInfoRow(sheet, rowNum++, "•", "Tất cả ngày tháng ở định dạng ISO (YYYY-MM-DD)", noteStyle);
        addInfoRow(sheet, rowNum++, "•", "Tỷ lệ phần trăm hiển thị dạng 0.00%", noteStyle);
        addInfoRow(sheet, rowNum++, "•", "Số sử dụng dấu phân cách hàng nghìn (1,000)", noteStyle);
        addInfoRow(sheet, rowNum++, "•", "Biểu đồ có tương tác trong Excel", noteStyle);
        addInfoRow(sheet, rowNum++, "•", "Bộ lọc được bật trên các trang dữ liệu", noteStyle);
        
        // Auto-size columns
        ExcelStyleUtil.setColumnWidths(sheet, 25, 60);
    }

    // ==================== HELPER METHODS ====================
    
    private int addMetricRow(XSSFSheet sheet, XSSFWorkbook workbook, int rowNum, 
                             String metric, long value, String status) {
        Row row = sheet.createRow(rowNum);
        boolean isAlternate = rowNum % 2 == 0;
        
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(metric);
        cell0.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
        
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(value);
        cell1.setCellStyle(ExcelStyleUtil.createNumberStyle(workbook, isAlternate));
        
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(status);
        cell2.setCellStyle(ExcelStyleUtil.createBadgeStyle(workbook, status));
        
        return rowNum + 1;
    }
    
    private int addMetricRowDecimal(XSSFSheet sheet, XSSFWorkbook workbook, int rowNum, 
                                    String metric, double value, String suffix, String status) {
        Row row = sheet.createRow(rowNum);
        boolean isAlternate = rowNum % 2 == 0;
        
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(metric);
        cell0.setCellStyle(ExcelStyleUtil.createDataStyle(workbook, isAlternate));
        
        Cell cell1 = row.createCell(1);
        if (suffix.equals("%")) {
            cell1.setCellValue(value / 100.0);
            cell1.setCellStyle(ExcelStyleUtil.createPercentageStyle(workbook, isAlternate));
        } else {
            cell1.setCellValue(value);
            cell1.setCellStyle(ExcelStyleUtil.createDecimalStyle(workbook, isAlternate));
        }
        
        Cell cell2 = row.createCell(2);
        cell2.setCellValue(status);
        cell2.setCellStyle(ExcelStyleUtil.createBadgeStyle(workbook, status));
        
        return rowNum + 1;
    }
    
    private void addInfoRow(XSSFSheet sheet, int rowNum, String key, String value, CellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(key);
        cell0.setCellStyle(style);
        
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(value);
        cell1.setCellStyle(style);
    }
}

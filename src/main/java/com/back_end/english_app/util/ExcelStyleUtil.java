package com.back_end.english_app.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

/**
 * Utility class for creating consistent, professional Excel styles
 * Theme: Corporate Blue - Enterprise-grade styling
 */
public class ExcelStyleUtil {
    
    // Corporate Blue Color Palette
    private static final java.awt.Color HEADER_BG = new java.awt.Color(31, 78, 120); // Dark Blue
    private static final java.awt.Color HEADER_TEXT = new java.awt.Color(255, 255, 255); // White
    private static final java.awt.Color ZEBRA_LIGHT = new java.awt.Color(255, 255, 255); // White
    private static final java.awt.Color ZEBRA_DARK = new java.awt.Color(247, 249, 252); // Light Grey
    private static final java.awt.Color BORDER_COLOR = new java.awt.Color(216, 223, 232); // Light Border
    private static final java.awt.Color ACCENT_BLUE = new java.awt.Color(41, 128, 185); // Accent Blue
    private static final java.awt.Color SUCCESS_GREEN = new java.awt.Color(39, 174, 96); // Success
    private static final java.awt.Color WARNING_ORANGE = new java.awt.Color(230, 126, 34); // Warning
    private static final java.awt.Color DANGER_RED = new java.awt.Color(231, 76, 60); // Danger
    
    /**
     * Create header style with dark blue background and white text
     */
    public static XSSFCellStyle createHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        
        // Font styling
        font.setFontName("Segoe UI");
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        // Background color
        style.setFillForegroundColor(new XSSFColor(HEADER_BG, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Borders
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        
        // Alignment
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        
        return style;
    }
    
    /**
     * Create title style for sheet titles
     */
    public static XSSFCellStyle createTitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        
        font.setFontName("Segoe UI");
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(new XSSFColor(HEADER_BG, null));
        style.setFont(font);
        
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Create subtitle style
     */
    public static XSSFCellStyle createSubtitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        
        font.setFontName("Segoe UI");
        font.setItalic(true);
        font.setFontHeightInPoints((short) 10);
        font.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFont(font);
        
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Create data style for light rows (zebra striping)
     */
    public static XSSFCellStyle createDataStyle(XSSFWorkbook workbook, boolean isAlternate) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        
        // Zebra striping
        if (isAlternate) {
            style.setFillForegroundColor(new XSSFColor(ZEBRA_DARK, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        
        // Borders
        setBorders(style);
        
        // Alignment
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Create number style with thousand separator
     */
    public static XSSFCellStyle createNumberStyle(XSSFWorkbook workbook, boolean isAlternate) {
        XSSFCellStyle style = createDataStyle(workbook, isAlternate);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
        return style;
    }
    
    /**
     * Create decimal number style (e.g., scores)
     */
    public static XSSFCellStyle createDecimalStyle(XSSFWorkbook workbook, boolean isAlternate) {
        XSSFCellStyle style = createDataStyle(workbook, isAlternate);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }
    
    /**
     * Create percentage style
     */
    public static XSSFCellStyle createPercentageStyle(XSSFWorkbook workbook, boolean isAlternate) {
        XSSFCellStyle style = createDataStyle(workbook, isAlternate);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        return style;
    }
    
    /**
     * Create date style
     */
    public static XSSFCellStyle createDateStyle(XSSFWorkbook workbook, boolean isAlternate) {
        XSSFCellStyle style = createDataStyle(workbook, isAlternate);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }
    
    /**
     * Create datetime style
     */
    public static XSSFCellStyle createDateTimeStyle(XSSFWorkbook workbook, boolean isAlternate) {
        XSSFCellStyle style = createDataStyle(workbook, isAlternate);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
        return style;
    }
    
    /**
     * Create currency style
     */
    public static XSSFCellStyle createCurrencyStyle(XSSFWorkbook workbook, boolean isAlternate) {
        XSSFCellStyle style = createDataStyle(workbook, isAlternate);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        return style;
    }
    
    /**
     * Create conditional style based on value
     * @param threshold - value threshold for color change
     * @param value - actual value to compare
     */
    public static XSSFCellStyle createConditionalNumberStyle(XSSFWorkbook workbook, boolean isAlternate, 
                                                              double threshold, double value) {
        XSSFCellStyle style = createNumberStyle(workbook, isAlternate);
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        
        if (value >= threshold) {
            font.setColor(new XSSFColor(SUCCESS_GREEN, null));
            font.setBold(true);
        } else {
            font.setColor(new XSSFColor(DANGER_RED, null));
        }
        
        style.setFont(font);
        return style;
    }
    
    /**
     * Create badge/tag style for status cells
     */
    public static XSSFCellStyle createBadgeStyle(XSSFWorkbook workbook, String status) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        
        font.setFontName("Segoe UI");
        font.setBold(true);
        font.setFontHeightInPoints((short) 9);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        
        // Color based on status
        switch (status.toUpperCase()) {
            case "ACTIVE":
            case "COMPLETED":
            case "SUCCESS":
                style.setFillForegroundColor(new XSSFColor(SUCCESS_GREEN, null));
                break;
            case "INACTIVE":
            case "PENDING":
            case "WARNING":
                style.setFillForegroundColor(new XSSFColor(WARNING_ORANGE, null));
                break;
            case "FAILED":
            case "ERROR":
            case "DANGER":
                style.setFillForegroundColor(new XSSFColor(DANGER_RED, null));
                break;
            default:
                style.setFillForegroundColor(new XSSFColor(ACCENT_BLUE, null));
        }
        
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        setBorders(style);
        
        return style;
    }
    
    /**
     * Apply thin borders to a style
     */
    private static void setBorders(XSSFCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        
        XSSFColor borderColor = new XSSFColor(BORDER_COLOR, null);
        style.setTopBorderColor(borderColor.getIndex());
        style.setRightBorderColor(borderColor.getIndex());
        style.setBottomBorderColor(borderColor.getIndex());
        style.setLeftBorderColor(borderColor.getIndex());
    }
    
    /**
     * Configure sheet for professional appearance
     */
    public static void configureSheet(XSSFSheet sheet) {
        // Disable gridlines for cleaner look
        sheet.setDisplayGridlines(false);
        
        // Set default column width
        sheet.setDefaultColumnWidth(15);
        
        // Set zoom level to 100%
        sheet.setZoom(100);
        
        // Freeze first row (header)
        sheet.createFreezePane(0, 1);
    }
    
    /**
     * Auto-size columns with max width limit
     */
    public static void autoSizeColumns(XSSFSheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
            
            // Limit maximum column width to 50 characters
            int currentWidth = sheet.getColumnWidth(i);
            int maxWidth = 50 * 256; // 50 characters
            if (currentWidth > maxWidth) {
                sheet.setColumnWidth(i, maxWidth);
            }
            
            // Set minimum column width to 10 characters
            int minWidth = 10 * 256;
            if (currentWidth < minWidth) {
                sheet.setColumnWidth(i, minWidth);
            }
        }
    }
    
    /**
     * Set specific column widths
     */
    public static void setColumnWidths(XSSFSheet sheet, int... widths) {
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i] * 256); // Width in characters
        }
    }
    
    /**
     * Create a merged cell with centered text
     */
    public static void createMergedCell(XSSFSheet sheet, int rowNum, int firstCol, int lastCol, 
                                       String value, XSSFCellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        
        sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(
            rowNum, rowNum, firstCol, lastCol
        ));
    }
    
    /**
     * Set header row height
     */
    public static void setHeaderHeight(XSSFSheet sheet, int rowNum, float height) {
        Row row = sheet.getRow(rowNum);
        if (row != null) {
            row.setHeightInPoints(height);
        }
    }
}

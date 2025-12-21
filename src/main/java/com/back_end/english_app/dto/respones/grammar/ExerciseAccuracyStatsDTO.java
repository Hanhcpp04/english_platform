package com.back_end.english_app.dto.respones.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thống kê độ chính xác và số câu đúng/sai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseAccuracyStatsDTO {

    // Thông tin cơ bản
    private Long userId;
    private Long lessonId;
    private Long typeId;
    private String typeName; // Tên loại bài tập (Multiple Choice, Fill in the Blank, etc.)

    // Thống kê số lượng
    private Long totalAnswers;        // Tổng số câu đã trả lời (có thể làm lại nhiều lần)
    private Long correctAnswers;      // Số câu trả lời đúng
    private Long incorrectAnswers;    // Số câu trả lời sai
    private Long distinctQuestions;   // Số câu hỏi unique đã trả lời

    // Tính toán độ chính xác
    private Double accuracyRate;      // Tỷ lệ chính xác (%) = (correctAnswers / totalAnswers) * 100
    private String accuracyGrade;     // Xếp loại: Excellent, Good, Average, Poor

    // Thông tin bổ sung
    private Integer totalQuestionsAvailable; // Tổng số câu hỏi có sẵn trong bài tập
    private Double completionRate;    // Tỷ lệ hoàn thành (%) = (distinctQuestions / totalQuestionsAvailable) * 100
}


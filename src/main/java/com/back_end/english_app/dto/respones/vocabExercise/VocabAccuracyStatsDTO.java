package com.back_end.english_app.dto.respones.vocabExercise;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO chứa thống kê độ chính xác và số câu đúng/sai cho bài tập từ vựng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabAccuracyStatsDTO {

    // Thông tin cơ bản
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("topic_id")
    private Long topicId;

    @JsonProperty("topic_name")
    private String topicName;

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("type_name")
    private String typeName;

    // Thống kê số lượng
    @JsonProperty("total_attempts")
    private Long totalAttempts;        // Tổng số lần trả lời (có thể làm lại nhiều lần)

    @JsonProperty("correct_answers")
    private Long correctAnswers;      // Số câu trả lời đúng

    @JsonProperty("incorrect_answers")
    private Long incorrectAnswers;    // Số câu trả lời sai

    @JsonProperty("distinct_questions")
    private Long distinctQuestions;   // Số câu hỏi unique đã trả lời

    // Tính toán độ chính xác
    @JsonProperty("accuracy_rate")
    private Double accuracyRate;      // Tỷ lệ chính xác (%) = (correctAnswers / totalAttempts) * 100

    @JsonProperty("accuracy_grade")
    private String accuracyGrade;     // Xếp loại: Excellent, Good, Average, Fair, Poor

    // Thông tin bổ sung
    @JsonProperty("total_questions_available")
    private Integer totalQuestionsAvailable; // Tổng số câu hỏi có sẵn trong bài tập

    @JsonProperty("completion_rate")
    private Double completionRate;    // Tỷ lệ hoàn thành (%) = (distinctQuestions / totalQuestionsAvailable) * 100

    @JsonProperty("total_xp_earned")
    private Long totalXpEarned;       // Tổng XP đã kiếm được
}


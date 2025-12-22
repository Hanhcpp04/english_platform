package com.back_end.english_app.controller.grammar;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.grammar.SubmitAnswerRequest;
import com.back_end.english_app.dto.respones.grammar.*;
import com.back_end.english_app.service.user.GrammarExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grammar/exercises")
@RequiredArgsConstructor
@Slf4j
public class GrammarExerciseController {

    private final GrammarExerciseService grammarExerciseService;

    /**
     * API 0: Lấy danh sách Exercise Types theo topic và lesson
     * GET /grammar/exercises/types?topic_id=1&lesson_id=1
     */
    @GetMapping("/types")
    public APIResponse<List<ExerciseTypeStructureDTO>> getExerciseTypes(
            @RequestParam("topic_id") Long topicId,
            @RequestParam("lesson_id") Long lessonId) {
        try {
            log.info("Getting exercise types for topic: {}, lesson: {}", topicId, lessonId);

            List<ExerciseTypeStructureDTO> types = grammarExerciseService.getExerciseTypes(topicId, lessonId);

            return APIResponse.success(types);
        } catch (Exception e) {
            log.error("Error getting exercise types: {}", e.getMessage(), e);
            return APIResponse.error("Failed to get exercise types: " + e.getMessage());
        }
    }

    /**
     * API 1: Lấy danh sách câu hỏi theo topic, lesson và type
     * GET /grammar/exercises/questions?topic_id=1&lesson_id=1&type_id=1
     */
    @GetMapping("/questions")
    public APIResponse<GrammarExerciseResponseDTO> getQuestions(
            @RequestParam("topic_id") Long topicId,
            @RequestParam("lesson_id") Long lessonId,
            @RequestParam("type_id") Long typeId) {
        try {
            log.info("Getting questions for topic: {}, lesson: {}, type: {}", topicId, lessonId, typeId);

            GrammarExerciseResponseDTO response = grammarExerciseService.getQuestions(topicId, lessonId, typeId);

            return APIResponse.success(response);
        } catch (Exception e) {
            log.error("Error getting questions: {}", e.getMessage(), e);
            return APIResponse.error("Failed to get questions: " + e.getMessage());
        }
    }

    /**
     * API 2: Submit câu trả lời và lưu tiến trình
     * POST /grammar/exercises/submit
     * Body: {
     *   "user_id": 1,
     *   "question_id": 101,
     *   "type_id": 1,
     *   "user_answer": "goes"
     * }
     */
    @PostMapping("/submit")
    public APIResponse<SubmitAnswerResponseDTO> submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request) {
        try {
            log.info("User {} submitting answer for question {}", request.getUserId(), request.getQuestionId());

            SubmitAnswerResponseDTO response = grammarExerciseService.submitAnswer(
                    request.getUserId(),
                    request.getQuestionId(),
                    request.getTypeId(),
                    request.getUserAnswer()
            );

            return APIResponse.success(response);
        } catch (RuntimeException e) {
            log.error("Error submitting answer: {}", e.getMessage(), e);
            return APIResponse.error("Failed to submit answer: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error submitting answer: {}", e.getMessage(), e);
            return APIResponse.error("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * API 3: Lấy lịch sử câu trả lời của user
     * GET /grammar/exercises/history?user_id=1&topic_id=1&lesson_id=1&type_id=1
     */
    @GetMapping("/history")
    public APIResponse<UserAnswerHistoryResponseDTO> getAnswerHistory(
            @RequestParam("user_id") Long userId,
            @RequestParam("topic_id") Long topicId,
            @RequestParam("lesson_id") Long lessonId,
            @RequestParam("type_id") Long typeId) {
        try {
            log.info("Getting answer history for user: {}, topic: {}, lesson: {}, type: {}",
                    userId, topicId, lessonId, typeId);

            UserAnswerHistoryResponseDTO response = grammarExerciseService.getAnswerHistory(
                    userId, topicId, lessonId, typeId
            );

            return APIResponse.success(response);
        } catch (Exception e) {
            log.error("Error getting answer history: {}", e.getMessage(), e);
            return APIResponse.error("Failed to get answer history: " + e.getMessage());
        }
    }

    /**
     * API 4: Reset câu trả lời của user theo lesson và type
     * DELETE /grammar/exercises/reset?user_id=1&lesson_id=1&type_id=1
     * Xóa tất cả câu trả lời đã làm trong một loại bài tập cụ thể của một lesson
     */
    @DeleteMapping("/reset")
    public APIResponse<String> resetExerciseAnswers(
            @RequestParam("user_id") Long userId,
            @RequestParam("lesson_id") Long lessonId,
            @RequestParam("type_id") Long typeId) {
        try {
            log.info("Resetting exercise answers for user: {}, lesson: {}, type: {}",
                    userId, lessonId, typeId);

            grammarExerciseService.resetExerciseAnswers(userId, lessonId, typeId);

            return APIResponse.success("Exercise answers reset successfully");
        } catch (Exception e) {
            log.error("Error resetting exercise answers: {}", e.getMessage(), e);
            return APIResponse.error("Failed to reset exercise answers: " + e.getMessage());
        }
    }

    /**
     * API 5: Lấy thống kê độ chính xác và số câu đúng/sai
     * GET /grammar/exercises/accuracy?user_id=1&lesson_id=1&type_id=1
     * Trả về thống kê: tổng số câu, số câu đúng/sai, tỷ lệ chính xác, xếp loại
     */
    @GetMapping("/accuracy")
    public APIResponse<ExerciseAccuracyStatsDTO> getExerciseAccuracyStats(
            @RequestParam("user_id") Long userId,
            @RequestParam("lesson_id") Long lessonId,
            @RequestParam("type_id") Long typeId) {
        try {
            log.info("Getting accuracy stats for user: {}, lesson: {}, type: {}",
                    userId, lessonId, typeId);

            ExerciseAccuracyStatsDTO stats = grammarExerciseService.getExerciseAccuracyStats(
                    userId, lessonId, typeId
            );

            return APIResponse.success(stats);
        } catch (Exception e) {
            log.error("Error getting accuracy stats: {}", e.getMessage(), e);
            return APIResponse.error("Failed to get accuracy stats: " + e.getMessage());
        }
    }
}


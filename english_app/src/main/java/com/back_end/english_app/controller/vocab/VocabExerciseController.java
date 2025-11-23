package com.back_end.english_app.controller.vocab;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.config.AppConfig;
import com.back_end.english_app.dto.request.vocabExercise.SubmitAnswerRequest;
import com.back_end.english_app.dto.request.vocabExercise.SubmitBatchRequest;
import com.back_end.english_app.dto.respones.vocabExercise.*;
import com.back_end.english_app.service.VocabExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vocab/exercise")
@RequiredArgsConstructor
public class VocabExerciseController {
    private final VocabExerciseService service;
    // lấy theo loại
    @GetMapping("/topics/{topicId}/exercise-types")
    public APIResponse<List<ExerciseTypeDTO>> getExerciseTypes(
            @PathVariable Integer topicId,
            @RequestParam Integer userId) {
        List<ExerciseTypeDTO> exerciseTypes = service.getExerciseTypesByTopic(topicId, userId);
        return APIResponse.success(exerciseTypes);
    }
    // lấy các câu hỏi theo từng topic và cho từng loại bt
    @GetMapping("/exercise-types/{typeId}/topics/{topicId}/questions")
    public APIResponse<QuestionsResponse> getQuestions(
            @PathVariable Integer typeId,
            @PathVariable Integer topicId,
            @RequestParam Integer userId) {

        QuestionsResponse questions = service.getQuestionsByType(typeId,topicId, userId);
        return APIResponse.success(questions);
    }
    // gửi đáp án
        @PostMapping("/questions/{questionId}/submit")
        public APIResponse<SubmitAnswerResponse> submitAnswer(
                @PathVariable Integer questionId,
                @Valid @RequestBody SubmitAnswerRequest request) {

            SubmitAnswerResponse response = service.submitAnswer(questionId, request);
            return APIResponse.success(response);
        }








    /**
     * 7. GET: Lấy lịch sử câu trả lời
     * GET /vocab/exercise/history?userId=1&topicId=1&typeId=1
     */
    @GetMapping("/history")
    public APIResponse<VocabAnswerHistoryResponseDTO> getAnswerHistory(
            @RequestParam Integer userId,
            @RequestParam Integer topicId,
            @RequestParam Integer typeId) {

        VocabAnswerHistoryResponseDTO history = service.getAnswerHistory(userId, topicId, typeId);
        return APIResponse.success(history);
    }

    /**
     * 8. DELETE: Reset bài tập - xóa lịch sử câu trả lời và progress
     * DELETE /vocab/exercise/reset?userId=1&topicId=1&typeId=1
     */
    @DeleteMapping("/reset")
    public APIResponse<Map<String, Object>> resetExerciseAnswers(
            @RequestParam Integer userId,
            @RequestParam Integer topicId,
            @RequestParam Integer typeId) {

        service.resetExerciseAnswers(userId, topicId, typeId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Exercise answers and progress reset successfully");

        return APIResponse.success(result);
    }

    /**
     * 9. GET: Thống kê độ chính xác
     * GET /vocab/exercise/accuracy?userId=1&topicId=1&typeId=1
     */
    @GetMapping("/accuracy")
    public APIResponse<VocabAccuracyStatsDTO> getExerciseAccuracyStats(
            @RequestParam Integer userId,
            @RequestParam Integer topicId,
            @RequestParam Integer typeId) {

        VocabAccuracyStatsDTO stats = service.getExerciseAccuracyStats(userId, topicId, typeId);
        return APIResponse.success(stats);
    }
}

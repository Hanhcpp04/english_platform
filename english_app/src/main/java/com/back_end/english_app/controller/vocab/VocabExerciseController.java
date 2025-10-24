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
    // submit
        @PostMapping("/questions/{questionId}/submit")
        public APIResponse<SubmitAnswerResponse> submitAnswer(
                @PathVariable Integer questionId,
                @Valid @RequestBody SubmitAnswerRequest request) {

            SubmitAnswerResponse response = service.submitAnswer(questionId, request);
            return APIResponse.success(response);
        }






    /**
     * 4. GET: Progress overview
     * GET /api/vocabulary/topics/{topicId}/progress?userId=1
     */
    @GetMapping("/topics/{topicId}/progress")
    public APIResponse<TopicProgressResponse> getTopicProgress(
            @PathVariable Integer topicId,
            @RequestParam Integer userId) {

        TopicProgressResponse progress = service.getTopicProgress(topicId, userId);
        return APIResponse.success(progress);
    }

    /**
     * 6. DELETE: Reset progress for exercise type
     * DELETE /api/vocabulary/exercise-types/{typeId}/progress?userId=1
     */
    @DeleteMapping("/exercise-types/{typeId}/progress")
    public APIResponse<Map<String, Object>> resetProgress(
            @PathVariable Integer typeId,
            @RequestParam Integer userId) {

        service.resetProgress(typeId, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "Progress reset successfully");

        return APIResponse.success(result);
    }
}

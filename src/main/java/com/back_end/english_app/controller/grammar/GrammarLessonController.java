package com.back_end.english_app.controller.grammar;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.grammar.CompleteLessonRequest;
import com.back_end.english_app.dto.respones.grammar.CompleteLessonResponseDTO;
import com.back_end.english_app.dto.respones.grammar.GrammarLessonResponseDTO;
import com.back_end.english_app.service.GrammarLessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grammar")
@RequiredArgsConstructor
@Slf4j
public class GrammarLessonController {

    private final GrammarLessonService grammarLessonService;

    @GetMapping("/topics/{topicId}/lessons")
    public APIResponse<GrammarLessonResponseDTO> getLessonsByTopic(
            @PathVariable Long topicId,
            @RequestParam("user_id") Long userId) {
        try {
            GrammarLessonResponseDTO response = grammarLessonService.getLessonsWithProgress(topicId, userId);

            return APIResponse.success(response);
        } catch (Exception e) {
            return APIResponse.error("Failed to fetch lessons: " + e.getMessage());
        }
    }

    @PostMapping("/lessons/complete")
    public APIResponse<CompleteLessonResponseDTO> completeLesson(
            @Valid @RequestBody CompleteLessonRequest request) {
        try {
            log.info("Received complete lesson request: {}", request);

            CompleteLessonResponseDTO response = grammarLessonService.completeLesson(
                    request.getUserId(),
                    request.getTopicId(),
                    request.getLessonId(),
                    request.getType()
            );

            return APIResponse.success(response);
        } catch (RuntimeException e) {
            log.error("Error completing lesson: {}", e.getMessage(), e);
            return APIResponse.error("Failed to complete lesson: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error completing lesson: {}", e.getMessage(), e);
            return APIResponse.error("An unexpected error occurred: " + e.getMessage());
        }
    }
}


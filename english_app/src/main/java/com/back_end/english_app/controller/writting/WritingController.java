package com.back_end.english_app.controller.writting;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.writing.WritingPromptResponse;
import com.back_end.english_app.dto.respones.writing.WritingTaskResponse;
import com.back_end.english_app.dto.respones.writing.WritingTopicResponse;
import com.back_end.english_app.service.WritingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/writing")
@RequiredArgsConstructor
@Slf4j
public class WritingController {

    private final WritingService writingService;
    @GetMapping("/topics")
    public APIResponse<List<WritingTopicResponse>> getAllTopics() {
        try {
            List<WritingTopicResponse> topics = writingService.getAllTopics();
            return APIResponse.success(topics);
        } catch (Exception e) {
            return APIResponse.error("Failed to fetch writing topics: " + e.getMessage());
        }
    }

    @GetMapping("/topics/{topicId}/tasks")
    public APIResponse<List<WritingTaskResponse>> getTasksByTopic(
            @PathVariable Integer topicId) {
        try {
            List<WritingTaskResponse> tasks = writingService.getTasksByTopicId(topicId);
            return APIResponse.success(tasks);
        } catch (RuntimeException e) {
            return APIResponse.error(e.getMessage());
        } catch (Exception e) {
            return APIResponse.error("Failed to fetch tasks: " + e.getMessage());
        }
    }
    @GetMapping("/tasks/{taskId}/prompts")
    public APIResponse<List<WritingPromptResponse>> getPromptsByTask(
            @PathVariable Integer taskId,
            @RequestParam("userId") Long userId) {
        try {
            List<WritingPromptResponse> prompts = writingService.getPromptsByTaskIdAndUserId(taskId, userId);
            return APIResponse.success(prompts);
        } catch (RuntimeException e) {
            return APIResponse.error(e.getMessage());
        } catch (Exception e) {
            return APIResponse.error("Failed to fetch prompts: " + e.getMessage());
        }
    }
}


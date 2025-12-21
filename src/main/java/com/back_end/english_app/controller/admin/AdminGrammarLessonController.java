package com.back_end.english_app.controller.admin;

import com.back_end.english_app.dto.request.grammar.AdminGrammarLessonRequest;
import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.grammar.AdminGrammarLessonResponse;
import com.back_end.english_app.service.admin.AdminGrammarLessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-grammar/lessons")
@RequiredArgsConstructor
public class AdminGrammarLessonController {

    private final AdminGrammarLessonService adminGrammarLessonService;

    /**
     * Lấy tất cả bài học
     * GET /admin-grammar/lessons/getAll?page=0&size=10
     */
    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminGrammarLessonResponse>>> getAllLessons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminGrammarLessonResponse> lessons = adminGrammarLessonService.getAllLessons(pageable);
        
        APIResponse<Page<AdminGrammarLessonResponse>> response = APIResponse.<Page<AdminGrammarLessonResponse>>builder()
                .code(1000)
                .result(lessons)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy bài học theo topic
     * GET /admin-grammar/lessons/by-topic/{topicId}?page=0&size=10
     */
    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<APIResponse<Page<AdminGrammarLessonResponse>>> getLessonsByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<AdminGrammarLessonResponse> lessons = adminGrammarLessonService.getLessonsByTopic(topicId, pageable);
        
        APIResponse<Page<AdminGrammarLessonResponse>> response = APIResponse.<Page<AdminGrammarLessonResponse>>builder()
                .code(1000)
                .result(lessons)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo bài học mới
     * POST /admin-grammar/lessons/create
     */
    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminGrammarLessonResponse>> createLesson(
            @RequestBody AdminGrammarLessonRequest request
    ) {
        AdminGrammarLessonResponse lesson = adminGrammarLessonService.createLesson(request);
        
        APIResponse<AdminGrammarLessonResponse> response = APIResponse.<AdminGrammarLessonResponse>builder()
                .code(1000)
                .result(lesson)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật bài học
     * PUT /admin-grammar/lessons/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminGrammarLessonResponse>> updateLesson(
            @PathVariable Long id,
            @RequestBody AdminGrammarLessonRequest request
    ) {
        AdminGrammarLessonResponse lesson = adminGrammarLessonService.updateLesson(id, request);
        
        APIResponse<AdminGrammarLessonResponse> response = APIResponse.<AdminGrammarLessonResponse>builder()
                .code(1000)
                .result(lesson)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa hoặc khôi phục bài học
     * PUT /admin-grammar/lessons/delete-restore/{id}/{status}
     */
    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<String>> deleteOrRestoreLesson(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        String message = adminGrammarLessonService.deleteOrRestoreLesson(id, status);
        
        APIResponse<String> response = APIResponse.<String>builder()
                .code(1000)
                .result(message)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

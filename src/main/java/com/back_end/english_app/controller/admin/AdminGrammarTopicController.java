package com.back_end.english_app.controller.admin;

import com.back_end.english_app.dto.request.grammar.AdminGrammarTopicRequest;
import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.grammar.AdminGrammarTopicResponse;
import com.back_end.english_app.service.admin.AdminGrammarTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-grammar/topics")
@RequiredArgsConstructor
public class AdminGrammarTopicController {

    private final AdminGrammarTopicService adminGrammarTopicService;

    /**
     * Lấy danh sách tất cả Grammar Topics
     * GET /admin-grammar/topics/getAll?page=0&size=10
     */
    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminGrammarTopicResponse>>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminGrammarTopicResponse> topics = adminGrammarTopicService.getAllTopics(pageable);
        
        APIResponse<Page<AdminGrammarTopicResponse>> response = APIResponse.<Page<AdminGrammarTopicResponse>>builder()
                .code(1000)
                .result(topics)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo Grammar Topic mới
     * POST /admin-grammar/topics/create
     */
    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminGrammarTopicResponse>> createTopic(
            @RequestBody AdminGrammarTopicRequest request
    ) {
        AdminGrammarTopicResponse topic = adminGrammarTopicService.createTopic(request);
        
        APIResponse<AdminGrammarTopicResponse> response = APIResponse.<AdminGrammarTopicResponse>builder()
                .code(1000)
                .result(topic)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật Grammar Topic
     * PUT /admin-grammar/topics/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminGrammarTopicResponse>> updateTopic(
            @PathVariable Long id,
            @RequestBody AdminGrammarTopicRequest request
    ) {
        AdminGrammarTopicResponse topic = adminGrammarTopicService.updateTopic(id, request);
        
        APIResponse<AdminGrammarTopicResponse> response = APIResponse.<AdminGrammarTopicResponse>builder()
                .code(1000)
                .result(topic)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa hoặc khôi phục Grammar Topic
     * PUT /admin-grammar/topics/delete-restore/{id}/{status}
     * status: "delete" hoặc "restore"
     */
    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<String>> deleteOrRestoreTopic(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        String message = adminGrammarTopicService.deleteOrRestoreTopic(id, status);
        
        APIResponse<String> response = APIResponse.<String>builder()
                .code(1000)
                .result(message)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.grammar.AdminExerciseGrammarTypeRequest;
import com.back_end.english_app.dto.respones.grammar.AdminExerciseGrammarTypeResponse;
import com.back_end.english_app.service.admin.AdminExerciseGrammarTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-grammar/exercise-types")
@RequiredArgsConstructor
public class AdminExerciseGrammarTypeController {

    private final AdminExerciseGrammarTypeService adminExerciseGrammarTypeService;

    /**
     * Lấy tất cả loại bài tập ngữ pháp
     * GET /admin-grammar/exercise-types/getAll?page=0&size=10
     */
    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminExerciseGrammarTypeResponse>>> getAllExerciseTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminExerciseGrammarTypeResponse> types = adminExerciseGrammarTypeService.getAllExerciseTypes(pageable);
        
        APIResponse<Page<AdminExerciseGrammarTypeResponse>> response = APIResponse.<Page<AdminExerciseGrammarTypeResponse>>builder()
                .code(1000)
                .result(types)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy loại bài tập theo topic
     * GET /admin-grammar/exercise-types/by-topic/{topicId}?page=0&size=10
     */
    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<APIResponse<Page<AdminExerciseGrammarTypeResponse>>> getExerciseTypesByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminExerciseGrammarTypeResponse> types = adminExerciseGrammarTypeService.getExerciseTypesByTopic(topicId, pageable);
        
        APIResponse<Page<AdminExerciseGrammarTypeResponse>> response = APIResponse.<Page<AdminExerciseGrammarTypeResponse>>builder()
                .code(1000)
                .result(types)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo loại bài tập ngữ pháp mới
     * POST /admin-grammar/exercise-types/create
     */
    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminExerciseGrammarTypeResponse>> createExerciseType(
            @RequestBody AdminExerciseGrammarTypeRequest request
    ) {
        AdminExerciseGrammarTypeResponse type = adminExerciseGrammarTypeService.createExerciseType(request);
        
        APIResponse<AdminExerciseGrammarTypeResponse> response = APIResponse.<AdminExerciseGrammarTypeResponse>builder()
                .code(1000)
                .result(type)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật loại bài tập ngữ pháp
     * PUT /admin-grammar/exercise-types/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminExerciseGrammarTypeResponse>> updateExerciseType(
            @PathVariable Long id,
            @RequestBody AdminExerciseGrammarTypeRequest request
    ) {
        AdminExerciseGrammarTypeResponse type = adminExerciseGrammarTypeService.updateExerciseType(id, request);
        
        APIResponse<AdminExerciseGrammarTypeResponse> response = APIResponse.<AdminExerciseGrammarTypeResponse>builder()
                .code(1000)
                .result(type)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa hoặc khôi phục loại bài tập ngữ pháp
     * PUT /admin-grammar/exercise-types/delete-restore/{id}/{status}
     * status: delete hoặc restore
     */
    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<AdminExerciseGrammarTypeResponse>> toggleExerciseTypeStatus(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        AdminExerciseGrammarTypeResponse type = adminExerciseGrammarTypeService.toggleExerciseTypeStatus(id, status);
        
        APIResponse<AdminExerciseGrammarTypeResponse> response = APIResponse.<AdminExerciseGrammarTypeResponse>builder()
                .code(1000)
                .result(type)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

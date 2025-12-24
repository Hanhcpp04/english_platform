package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabExerciseTypeRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabExerciseTypeResponse;
import com.back_end.english_app.service.admin.AdminVocabExerciseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-vocab/exercise-types")
@RequiredArgsConstructor
public class AdminVocabExerciseTypeController {

    private final AdminVocabExerciseTypeService adminVocabExerciseTypeService;

    /**
     * Lấy tất cả loại bài tập từ vựng
     * GET /admin-vocab/exercise-types/getAll?page=0&size=10
     */
    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminVocabExerciseTypeResponse>>> getAllExerciseTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminVocabExerciseTypeResponse> types = adminVocabExerciseTypeService.getAllExerciseTypes(pageable);
        
        APIResponse<Page<AdminVocabExerciseTypeResponse>> response = APIResponse.<Page<AdminVocabExerciseTypeResponse>>builder()
                .code(1000)
                .result(types)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo loại bài tập từ vựng mới
     * POST /admin-vocab/exercise-types/create
     */
    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminVocabExerciseTypeResponse>> createExerciseType(
            @RequestBody AdminVocabExerciseTypeRequest request
    ) {
        AdminVocabExerciseTypeResponse type = adminVocabExerciseTypeService.createExerciseType(request);
        
        APIResponse<AdminVocabExerciseTypeResponse> response = APIResponse.<AdminVocabExerciseTypeResponse>builder()
                .code(1000)
                .result(type)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật loại bài tập từ vựng
     * PUT /admin-vocab/exercise-types/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminVocabExerciseTypeResponse>> updateExerciseType(
            @PathVariable Long id,
            @RequestBody AdminVocabExerciseTypeRequest request
    ) {
        AdminVocabExerciseTypeResponse type = adminVocabExerciseTypeService.updateExerciseType(id, request);
        
        APIResponse<AdminVocabExerciseTypeResponse> response = APIResponse.<AdminVocabExerciseTypeResponse>builder()
                .code(1000)
                .result(type)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa hoặc khôi phục loại bài tập từ vựng
     * PUT /admin-vocab/exercise-types/delete-restore/{id}/{status}
     * status: delete hoặc restore
     */
    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<AdminVocabExerciseTypeResponse>> toggleExerciseTypeStatus(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        AdminVocabExerciseTypeResponse type = adminVocabExerciseTypeService.toggleExerciseTypeStatus(id, status);
        
        APIResponse<AdminVocabExerciseTypeResponse> response = APIResponse.<AdminVocabExerciseTypeResponse>builder()
                .code(1000)
                .result(type)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

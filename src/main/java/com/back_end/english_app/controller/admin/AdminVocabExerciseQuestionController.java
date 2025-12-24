package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabExerciseQuestionRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabExerciseQuestionResponse;
import com.back_end.english_app.service.admin.AdminVocabExerciseQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-vocab/exercise-questions")
@RequiredArgsConstructor
public class AdminVocabExerciseQuestionController {

    private final AdminVocabExerciseQuestionService adminVocabExerciseQuestionService;

    /**
     * Lấy tất cả câu hỏi bài tập từ vựng
     * GET /admin-vocab/exercise-questions/getAll?page=0&size=10
     */
    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminVocabExerciseQuestionResponse>>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminVocabExerciseQuestionResponse> questions = adminVocabExerciseQuestionService.getAllQuestions(pageable);
        
        APIResponse<Page<AdminVocabExerciseQuestionResponse>> response = APIResponse.<Page<AdminVocabExerciseQuestionResponse>>builder()
                .code(1000)
                .result(questions)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy câu hỏi theo loại bài tập
     * GET /admin-vocab/exercise-questions/by-type/{typeId}?page=0&size=10
     */
    @GetMapping("/by-type/{typeId}")
    public ResponseEntity<APIResponse<Page<AdminVocabExerciseQuestionResponse>>> getQuestionsByType(
            @PathVariable Long typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminVocabExerciseQuestionResponse> questions = adminVocabExerciseQuestionService.getQuestionsByType(typeId, pageable);
        
        APIResponse<Page<AdminVocabExerciseQuestionResponse>> response = APIResponse.<Page<AdminVocabExerciseQuestionResponse>>builder()
                .code(1000)
                .result(questions)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy câu hỏi theo topic
     * GET /admin-vocab/exercise-questions/by-topic/{topicId}?page=0&size=10
     */
    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<APIResponse<Page<AdminVocabExerciseQuestionResponse>>> getQuestionsByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminVocabExerciseQuestionResponse> questions = adminVocabExerciseQuestionService.getQuestionsByTopic(topicId, pageable);
        
        APIResponse<Page<AdminVocabExerciseQuestionResponse>> response = APIResponse.<Page<AdminVocabExerciseQuestionResponse>>builder()
                .code(1000)
                .result(questions)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo câu hỏi bài tập từ vựng mới
     * POST /admin-vocab/exercise-questions/create
     */
    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminVocabExerciseQuestionResponse>> createQuestion(
            @RequestBody AdminVocabExerciseQuestionRequest request
    ) {
        AdminVocabExerciseQuestionResponse question = adminVocabExerciseQuestionService.createQuestion(request);
        
        APIResponse<AdminVocabExerciseQuestionResponse> response = APIResponse.<AdminVocabExerciseQuestionResponse>builder()
                .code(1000)
                .result(question)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật câu hỏi bài tập từ vựng
     * PUT /admin-vocab/exercise-questions/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminVocabExerciseQuestionResponse>> updateQuestion(
            @PathVariable Long id,
            @RequestBody AdminVocabExerciseQuestionRequest request
    ) {
        AdminVocabExerciseQuestionResponse question = adminVocabExerciseQuestionService.updateQuestion(id, request);
        
        APIResponse<AdminVocabExerciseQuestionResponse> response = APIResponse.<AdminVocabExerciseQuestionResponse>builder()
                .code(1000)
                .result(question)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa hoặc khôi phục câu hỏi
     * PUT /admin-vocab/exercise-questions/delete-restore/{id}/{status}
     * status: delete hoặc restore
     */
    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<AdminVocabExerciseQuestionResponse>> toggleQuestionStatus(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        AdminVocabExerciseQuestionResponse question = adminVocabExerciseQuestionService.toggleQuestionStatus(id, status);
        
        APIResponse<AdminVocabExerciseQuestionResponse> response = APIResponse.<AdminVocabExerciseQuestionResponse>builder()
                .code(1000)
                .result(question)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

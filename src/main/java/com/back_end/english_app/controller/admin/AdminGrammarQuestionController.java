package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.grammar.AdminGrammarQuestionRequest;
import com.back_end.english_app.dto.respones.grammar.AdminGrammarQuestionResponse;
import com.back_end.english_app.service.admin.AdminGrammarQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-grammar/exercise-questions")
@RequiredArgsConstructor
public class AdminGrammarQuestionController {

    private final AdminGrammarQuestionService adminGrammarQuestionService;

    /**
     * Lấy tất cả câu hỏi bài tập ngữ pháp
     * GET /admin-grammar/exercise-questions/getAll?page=0&size=10
     */
    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminGrammarQuestionResponse>>> getAllQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminGrammarQuestionResponse> questions = adminGrammarQuestionService.getAllQuestions(pageable);
        
        APIResponse<Page<AdminGrammarQuestionResponse>> response = APIResponse.<Page<AdminGrammarQuestionResponse>>builder()
                .code(1000)
                .result(questions)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy câu hỏi theo loại bài tập
     * GET /admin-grammar/exercise-questions/by-type/{typeId}?page=0&size=10
     */
    @GetMapping("/by-type/{typeId}")
    public ResponseEntity<APIResponse<Page<AdminGrammarQuestionResponse>>> getQuestionsByType(
            @PathVariable Long typeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminGrammarQuestionResponse> questions = adminGrammarQuestionService.getQuestionsByType(typeId, pageable);
        
        APIResponse<Page<AdminGrammarQuestionResponse>> response = APIResponse.<Page<AdminGrammarQuestionResponse>>builder()
                .code(1000)
                .result(questions)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy câu hỏi theo lesson
     * GET /admin-grammar/exercise-questions/by-lesson/{lessonId}?page=0&size=10
     */
    @GetMapping("/by-lesson/{lessonId}")
    public ResponseEntity<APIResponse<Page<AdminGrammarQuestionResponse>>> getQuestionsByLesson(
            @PathVariable Long lessonId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminGrammarQuestionResponse> questions = adminGrammarQuestionService.getQuestionsByLesson(lessonId, pageable);
        
        APIResponse<Page<AdminGrammarQuestionResponse>> response = APIResponse.<Page<AdminGrammarQuestionResponse>>builder()
                .code(1000)
                .result(questions)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tạo câu hỏi bài tập ngữ pháp mới
     * POST /admin-grammar/exercise-questions/create
     */
    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminGrammarQuestionResponse>> createQuestion(
            @RequestBody AdminGrammarQuestionRequest request
    ) {
        AdminGrammarQuestionResponse question = adminGrammarQuestionService.createQuestion(request);
        
        APIResponse<AdminGrammarQuestionResponse> response = APIResponse.<AdminGrammarQuestionResponse>builder()
                .code(1000)
                .result(question)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật câu hỏi bài tập ngữ pháp
     * PUT /admin-grammar/exercise-questions/update/{id}
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminGrammarQuestionResponse>> updateQuestion(
            @PathVariable Long id,
            @RequestBody AdminGrammarQuestionRequest request
    ) {
        AdminGrammarQuestionResponse question = adminGrammarQuestionService.updateQuestion(id, request);
        
        APIResponse<AdminGrammarQuestionResponse> response = APIResponse.<AdminGrammarQuestionResponse>builder()
                .code(1000)
                .result(question)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * Xóa hoặc khôi phục câu hỏi
     * PUT /admin-grammar/exercise-questions/delete-restore/{id}/{status}
     * status: delete hoặc restore
     */
    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<AdminGrammarQuestionResponse>> toggleQuestionStatus(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        AdminGrammarQuestionResponse question = adminGrammarQuestionService.toggleQuestionStatus(id, status);
        
        APIResponse<AdminGrammarQuestionResponse> response = APIResponse.<AdminGrammarQuestionResponse>builder()
                .code(1000)
                .result(question)
                .build();
        
        return ResponseEntity.ok(response);
    }
}

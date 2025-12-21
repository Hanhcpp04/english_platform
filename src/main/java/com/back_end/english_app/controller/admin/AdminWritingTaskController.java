package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.writing.AdminGradingCriteriaRequest;
import com.back_end.english_app.dto.request.writing.AdminWritingTaskRequest;
import com.back_end.english_app.dto.respones.writing.AdminGradingCriteriaResponse;
import com.back_end.english_app.dto.respones.writing.AdminWritingTaskResponse;
import com.back_end.english_app.service.admin.AdminWritingTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-writing/tasks")
@RequiredArgsConstructor
public class AdminWritingTaskController {

    private final AdminWritingTaskService adminWritingTaskService;

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminWritingTaskResponse>>> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminWritingTaskResponse> tasks = adminWritingTaskService.getAllTasks(pageable);
        
        return ResponseEntity.ok(APIResponse.<Page<AdminWritingTaskResponse>>builder()
                .code(1000)
                .result(tasks)
                .build());
    }

    @GetMapping("/by-topic/{topicId}")
    public ResponseEntity<APIResponse<Page<AdminWritingTaskResponse>>> getTasksByTopic(
            @PathVariable Integer topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminWritingTaskResponse> tasks = adminWritingTaskService.getTasksByTopic(topicId, pageable);
        
        return ResponseEntity.ok(APIResponse.<Page<AdminWritingTaskResponse>>builder()
                .code(1000)
                .result(tasks)
                .build());
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminWritingTaskResponse>> createTask(
            @RequestBody AdminWritingTaskRequest request
    ) {
        AdminWritingTaskResponse response = adminWritingTaskService.createTask(request);
        
        return ResponseEntity.ok(APIResponse.<AdminWritingTaskResponse>builder()
                .code(1000)
                .result(response)
                .build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminWritingTaskResponse>> updateTask(
            @PathVariable Integer id,
            @RequestBody AdminWritingTaskRequest request
    ) {
        AdminWritingTaskResponse response = adminWritingTaskService.updateTask(id, request);
        
        return ResponseEntity.ok(APIResponse.<AdminWritingTaskResponse>builder()
                .code(1000)
                .result(response)
                .build());
    }

    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<Void>> deleteOrRestoreTask(
            @PathVariable Integer id,
            @PathVariable String status
    ) {
        adminWritingTaskService.deleteOrRestoreTask(id, status);
        
        return ResponseEntity.ok(APIResponse.<Void>builder()
                .code(1000)
                .message(status.equals("delete") ? "Task deleted successfully" : "Task restored successfully")
                .build());
    }

    @PutMapping("/{taskId}/grading-criteria")
    public ResponseEntity<APIResponse<AdminGradingCriteriaResponse>> updateGradingCriteria(
            @PathVariable Integer taskId,
            @RequestBody AdminGradingCriteriaRequest request
    ) {
        AdminGradingCriteriaResponse response = adminWritingTaskService.updateGradingCriteria(taskId, request);
        
        return ResponseEntity.ok(APIResponse.<AdminGradingCriteriaResponse>builder()
                .code(1000)
                .message("Grading criteria updated successfully")
                .result(response)
                .build());
    }
}

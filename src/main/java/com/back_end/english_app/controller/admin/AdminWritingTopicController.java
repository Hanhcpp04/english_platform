package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.writing.AdminWritingTopicRequest;
import com.back_end.english_app.dto.respones.writing.AdminWritingTopicResponse;
import com.back_end.english_app.service.admin.AdminWritingTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-writing/topics")
@RequiredArgsConstructor
public class AdminWritingTopicController {

    private final AdminWritingTopicService adminWritingTopicService;

    @GetMapping("/getAll")
    public ResponseEntity<APIResponse<Page<AdminWritingTopicResponse>>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AdminWritingTopicResponse> topics = adminWritingTopicService.getAllTopics(pageable);
        
        return ResponseEntity.ok(APIResponse.<Page<AdminWritingTopicResponse>>builder()
                .code(1000)
                .result(topics)
                .build());
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<AdminWritingTopicResponse>> createTopic(
            @RequestBody AdminWritingTopicRequest request
    ) {
        AdminWritingTopicResponse response = adminWritingTopicService.createTopic(request);
        
        return ResponseEntity.ok(APIResponse.<AdminWritingTopicResponse>builder()
                .code(1000)
                .result(response)
                .build());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<APIResponse<AdminWritingTopicResponse>> updateTopic(
            @PathVariable Integer id,
            @RequestBody AdminWritingTopicRequest request
    ) {
        AdminWritingTopicResponse response = adminWritingTopicService.updateTopic(id, request);
        
        return ResponseEntity.ok(APIResponse.<AdminWritingTopicResponse>builder()
                .code(1000)
                .result(response)
                .build());
    }

    @PutMapping("/delete-restore/{id}/{status}")
    public ResponseEntity<APIResponse<Void>> deleteOrRestoreTopic(
            @PathVariable Integer id,
            @PathVariable String status
    ) {
        adminWritingTopicService.deleteOrRestoreTopic(id, status);
        
        return ResponseEntity.ok(APIResponse.<Void>builder()
                .code(1000)
                .message(status.equals("delete") ? "Topic deleted successfully" : "Topic restored successfully")
                .build());
    }
}

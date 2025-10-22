package com.back_end.english_app.controller.vocab;


import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabTopicResponse;
import com.back_end.english_app.service.VocabTopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/topic")
public class AdminVocabController {
    private final VocabTopicService vocabTopicService;

    @GetMapping
    public List<AdminVocabTopicResponse> getAllTopics() {
        return vocabTopicService.getAllTopics();
    }

    @PostMapping(value="", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<AdminVocabTopicResponse> createTopic(
            @RequestPart("data") AdminVocabTopicRequest request,
            @RequestPart("iconUrl") MultipartFile iconUrl
    ) {
        try {
            AdminVocabTopicResponse topic = vocabTopicService.createTopic(request, iconUrl);
            return APIResponse.<AdminVocabTopicResponse>builder()
                    .code(1000)
                    .message("Tạo thành công")
                    .result(topic)
                    .build();
        } catch (Exception e) {
            return APIResponse.error("Lỗi khi tạo chủ đề: " + e.getMessage());
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<AdminVocabTopicResponse> updateTopic(
            @PathVariable Long id,
            @RequestPart("data") AdminVocabTopicUpdateRequest request,
            @RequestPart(value = "iconUrl", required = false) MultipartFile iconUrl) {
        try {
            AdminVocabTopicResponse topic = vocabTopicService.updateTopic(id, request, iconUrl);
            return APIResponse.success(topic);
        } catch (Exception e) {
            return APIResponse.error("Lỗi khi cập nhật chủ đề: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public APIResponse<String> deleteTopic1(@PathVariable Long id) {
        try {
            vocabTopicService.softDeleteTopic(id);
            return APIResponse.<String>builder()
                    .code(1000)
                    .message("Xoá thành công")
                    .result("Chủ đề đã được vô hiệu hóa")
                    .build();
        } catch (RuntimeException e) {
            return APIResponse.<String>builder()
                    .code(404)
                    .message(e.getMessage())
                    .build();

        } catch (Exception e) {
            return APIResponse.error("Đã xảy ra lỗi khi xoá chủ đề: " + e.getMessage());
        }
    }

    //Tim kiem theo ten tieng viet hoac tieng anh
    @GetMapping("/search")
    public List<AdminVocabTopicResponse> searchTopics(@RequestParam String keyword) {
        return vocabTopicService.searchTopics(keyword);
    }
}

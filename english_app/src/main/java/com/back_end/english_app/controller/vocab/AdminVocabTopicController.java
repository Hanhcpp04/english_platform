package com.back_end.english_app.controller.vocab;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabTopicResponse;
import com.back_end.english_app.service.AdminVocabTopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin-vocab")
@RequiredArgsConstructor
public class AdminVocabTopicController {
    private final AdminVocabTopicService adminVocabTopicService;

    @GetMapping("/topics")
    public APIResponse<Page<AdminVocabTopicResponse>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return adminVocabTopicService.getAllTopics(page, size);
    }

    @PostMapping(value = "/topics", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> addNewVocabTopic(
            @RequestPart("data") AdminVocabTopicRequest request,
            @RequestPart("icon")MultipartFile iconFile
    ){
        return adminVocabTopicService.addNewVocabTopic(request, iconFile);
    }

    @PutMapping(value = "/topics/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> updateVocabTopic(
            @PathVariable Long id,
            @RequestPart("data") AdminVocabTopicUpdateRequest request,
            @RequestPart(value = "icon", required = false) MultipartFile iconFile
    ) {
        return adminVocabTopicService.updateVocabTopic(id, request, iconFile);
    }
}

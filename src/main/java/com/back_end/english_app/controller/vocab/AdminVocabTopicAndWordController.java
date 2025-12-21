package com.back_end.english_app.controller.vocab;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabTopicResponse;
import com.back_end.english_app.dto.respones.vocab.AdminVocabWordResponse;
import com.back_end.english_app.service.AdminVocabTopicService;
import com.back_end.english_app.service.AdminVocabWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin-vocab")
@RequiredArgsConstructor
public class AdminVocabTopicAndWordController {
    private final AdminVocabTopicService adminVocabTopicService;
    private final AdminVocabWordService adminVocabWordService;
//topic

    //get all
    @GetMapping("/topics/getAll")
    public APIResponse<Page<AdminVocabTopicResponse>> getAllTopics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return adminVocabTopicService.getAllTopics(page, size);
    }

    //tạo vocab topic mới
    @PostMapping("/topics/create")
    public APIResponse<?> addNewVocabTopic(
            @RequestBody AdminVocabTopicRequest request
    ){
        return adminVocabTopicService.addNewVocabTopic(request);
    }

    //update vocab topic
    @PutMapping("/topics/update/{id}")
    public APIResponse<?> updateVocabTopic(
            @PathVariable Long id,
            @RequestBody AdminVocabTopicRequest request
    ) {
        return adminVocabTopicService.updateVocabTopic(id, request);
    }

    //xóa hoặc khôi phục vocab topic
    @PutMapping("/topics/delete-restore/{id}/{status}")
    public APIResponse<String> deleteOrRestoreVocabTopicById(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        return adminVocabTopicService.deleteOrRestoreVocabTopic(id, status);
    }

//word

    //get all
    @GetMapping("/words/getAll")
    public APIResponse<Page<AdminVocabWordResponse>> getAllWords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return adminVocabWordService.getAllWords(page, size);
    }

    //get words by topic
    @GetMapping("/words/by-topic/{topicId}")
    public APIResponse<Page<AdminVocabWordResponse>> getWordsByTopic(
            @PathVariable Long topicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return adminVocabWordService.getWordsByTopic(topicId, page, size);
    }

    //create new word
    @PostMapping("/words/create")
    public APIResponse<?> addNewWord(
            @RequestBody AdminVocabWordRequest request
    ){
        return adminVocabWordService.addNewVocabWord(request);
    }

    //update word
    @PutMapping("/words/update/{id}")
    public APIResponse<?> updateWord(
            @PathVariable Long id,
            @RequestBody AdminVocabWordUpdateRequest request
    ) {
        return adminVocabWordService.updateVocabWord(id, request);
    }

    //xóa hoặc khôi phục từ vựng
    @PutMapping("/words/delete-restore/{id}/{status}")
    public APIResponse<String> deleteOrRestoreWordById(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        return adminVocabWordService.deleteOrRestoreVocabWord(id, status);
    }

}

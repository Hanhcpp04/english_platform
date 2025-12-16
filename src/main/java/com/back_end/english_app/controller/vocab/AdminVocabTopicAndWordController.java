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
    @PostMapping(value = "/topics/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> addNewVocabTopic(
            @RequestPart("data") AdminVocabTopicRequest request,
            @RequestPart("icon") MultipartFile iconFile
    ){
        return adminVocabTopicService.addNewVocabTopic(request, iconFile);
    }

    //update vocab topic
    @PutMapping(value = "/topics/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> updateVocabTopic(
            @PathVariable Long id,
            @RequestPart("data") AdminVocabTopicRequest request,
            @RequestPart(value = "icon", required = false) MultipartFile iconFile
    ) {
        return adminVocabTopicService.updateVocabTopic(id, request, iconFile);
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

    //create new word
    @PostMapping(value = "words/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> addNewWord(
            @RequestPart("data") AdminVocabWordRequest request,
            @RequestPart("audio") MultipartFile audioFile,
            @RequestPart("image") MultipartFile imageFile
    ){
        return adminVocabWordService.addNewVocabWord(request, audioFile, imageFile);
    }

    //update word
    @PutMapping(value = "/words/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> updateWord(
            @PathVariable Long id,
            @RequestPart("data") AdminVocabWordUpdateRequest request,
            @RequestPart(value = "audio", required = false) MultipartFile audioFile,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return adminVocabWordService.updateVocabWord(id, request, audioFile, imageFile);
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

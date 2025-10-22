package com.back_end.english_app.controller.vocab;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabWordResponse;
import com.back_end.english_app.entity.VocabWordEntity;
import com.back_end.english_app.service.VocabTopicService;
import com.back_end.english_app.service.VocabWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/word")
public class AdminWordController {
    private final VocabWordService vocabWordService;

    //hien thi tat ca tu vung co phan trang
    @GetMapping
    public ResponseEntity<APIResponse<Page<AdminVocabWordResponse>>> getAllWords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<AdminVocabWordResponse> result = vocabWordService.getAllWords(page, size);
            return ResponseEntity.ok(APIResponse.success(result));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.error("Đã xảy ra lỗi: " + e.getMessage()));
        }
    }

    //them tu vung moi
    @PostMapping("")
    public APIResponse<AdminVocabWordResponse> createWord(
            @RequestPart("data")AdminVocabWordRequest request,
            @RequestPart("imageFile") MultipartFile imageFile,
            @RequestPart("audioFile") MultipartFile audioFile
            ){
        try{
            AdminVocabWordResponse newword = vocabWordService.createWord(request, imageFile, audioFile);
            return APIResponse.<AdminVocabWordResponse>builder()
                    .code(1000)
                    .message("Tạo từ vựng thành công")
                    .result(newword)
                    .build();
        }catch(Exception e){
            return APIResponse.error("Lỗi khi tạo từ vựng: " + e.getMessage());
        }
    }

    //update tu vung
    @PutMapping("/{id}")
    public APIResponse<AdminVocabWordResponse> updateWord(
            @PathVariable Long id,
            @RequestPart("data") AdminVocabWordUpdateRequest request,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile
    ){
        try{
            AdminVocabWordResponse updatedWord = vocabWordService.updateWord(id, request, imageFile, audioFile);
            return APIResponse.<AdminVocabWordResponse>builder()
                    .code(1000)
                    .message("Cập nhật từ vựng thành công")
                    .result(updatedWord)
                    .build();
        }catch(Exception e){
            return APIResponse.error("Lỗi khi cập nhật từ vựng: " + e.getMessage());
        }
    }

    //xoa tu vung
    @DeleteMapping("/{id}")
    public APIResponse<String> deleteWord(@PathVariable Long id) {
        try {
            vocabWordService.softDeleteWord(id);
            return APIResponse.<String>builder()
                    .code(1000)
                    .message("Xoá từ vựng thành công")
                    .result("Từ vựng đã được vô hiệu hóa")
                    .build();
        } catch (RuntimeException e) {
            return APIResponse.<String>builder()
                    .code(404)
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return APIResponse.error("Đã xảy ra lỗi khi xoá từ vựng: " + e.getMessage());
        }
    }
}

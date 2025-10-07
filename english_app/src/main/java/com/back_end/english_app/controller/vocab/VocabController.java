package com.back_end.english_app.controller.vocab;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.CompleteWordRequest;
import com.back_end.english_app.dto.respones.vocab.VocabWordResponse;
import com.back_end.english_app.service.VocabWordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vocab")
@RequiredArgsConstructor
public class VocabController {

    private final VocabWordService vocabWordService;

//     API lấy tất cả từ vựng theo topic với trạng thái hoàn thành
    @GetMapping("/topic/{topicId}/words")
    public APIResponse<List<VocabWordResponse>> getWordsByTopic(
            @PathVariable Long topicId,
            @RequestParam Long userId) {

        List<VocabWordResponse> words = vocabWordService.getWordsByTopicWithProgress(topicId, userId);

        return APIResponse.<List<VocabWordResponse>>builder()
                .code(200)
                .message("Lấy danh sách từ vựng thành công")
                .result(words)
                .build();
    }
//     API đánh dấu từ vựng đã hoàn thành
    @PostMapping("/complete")
    public APIResponse<VocabWordResponse> completeWord(
            @RequestBody CompleteWordRequest request,
            @RequestParam Long userId) {
        VocabWordResponse response = vocabWordService.completeWord(request, userId);
        return APIResponse.<VocabWordResponse>builder()
                        .code(200)
                        .message("Đánh dấu hoàn thành thành công và cộng " + response.getXpReward() + " XP")
                        .result(response)
                        .build();
    }
}

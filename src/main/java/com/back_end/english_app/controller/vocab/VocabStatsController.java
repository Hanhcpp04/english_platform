package com.back_end.english_app.controller.vocab;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.dashboard.TopicWithProgressDTO;
import com.back_end.english_app.dto.respones.dashboard.VocabStatsDTO;
import com.back_end.english_app.dto.respones.dashboard.VocabTopicDTO;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.service.VocabStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vocab")
@RequiredArgsConstructor
public class VocabStatsController {
    private  final VocabStatsService vocabStatsService;
    private final VocabTopicRepository vocabTopicRepository;
    //    Lấy thống kê từ vựng của user
    @GetMapping("/stats/{userId}")
    public APIResponse<VocabStatsDTO> getUserVocabStats(@PathVariable Long userId){
        VocabStatsDTO stats = vocabStatsService.getUserVocabStats(userId);
        return APIResponse.<VocabStatsDTO>builder()
                .code(1000)
                .result(stats)
                .build();
    }
    // lấy tất cả các topic
    @GetMapping("/topics")
    public APIResponse<List<VocabTopicDTO>> getAllTopic(){
        List<VocabTopicDTO> topic = vocabStatsService.getAllTopics();
        return APIResponse.<List<VocabTopicDTO>>builder()
                .code(1000)
                .result(topic)
                .build();
    }
    //lay thong tin chi tiet mot topic
    @GetMapping("topics/{id}")
    public  APIResponse<VocabTopicDTO> getTopic( @PathVariable Long id){
        boolean exist = vocabTopicRepository.existsById(id);
        if (!exist){
           return APIResponse.notFound("Khong tim thay topic !");
        }
        VocabTopicDTO topic = vocabStatsService.getTopicById(id);
        return APIResponse.<VocabTopicDTO>builder()
                .code(1000)
                .result(topic)
                .build();
    }
    // lay topic theo tien trinh cua nguoi dung
    @GetMapping("/topics/progress/{userId}")
    public APIResponse<List<TopicWithProgressDTO>> getAllTopicsWithProgress(@PathVariable Long userId) {
        List<TopicWithProgressDTO> topics = vocabStatsService.getAllTopicsWithProgress(userId);
        return APIResponse.<List<TopicWithProgressDTO>>builder()
                .code(1000)
                .result(topics)
                .build();
    }

}

package com.back_end.english_app.controller.grammar;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.grammar.GrammarStatsDTO;
import com.back_end.english_app.service.GrammarStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grammar")
@RequiredArgsConstructor
@Slf4j
public class GrammarStatsController {
    
    private final GrammarStatsService grammarStatsService;

    /**
     * Get grammar statistics for a specific user
     * Returns total lessons learned, topics completed, XP earned, and detailed topic progress
     * 
     * @param userId The ID of the user
     * @return APIResponse containing grammar statistics
     */
    @GetMapping("/stats/{userId}")
    public APIResponse<GrammarStatsDTO> getUserGrammarStats(@PathVariable Long userId) {
        log.info("Fetching grammar stats for userId: {}", userId);
        
        GrammarStatsDTO stats = grammarStatsService.getUserGrammarStats(userId);
        
        return APIResponse.<GrammarStatsDTO>builder()
                .code(1000)
                .result(stats)
                .build();
    }

    /**
     * Get all grammar topics with progress for a specific user
     * 
     * @param userId The ID of the user
     * @return APIResponse containing list of topics with progress
     */
    @GetMapping("/topics/progress/{userId}")
    public APIResponse<List<GrammarStatsDTO.GrammarTopicProgressDTO>> getAllTopicsWithProgress(@PathVariable Long userId) {
        log.info("Fetching all grammar topics with progress for userId: {}", userId);
        
        List<GrammarStatsDTO.GrammarTopicProgressDTO> topics = grammarStatsService.getAllTopicsWithProgress(userId);
        
        return APIResponse.<List<GrammarStatsDTO.GrammarTopicProgressDTO>>builder()
                .code(1000)
                .result(topics)
                .build();
    }

    /**
     * Get specific topic progress for a user
     * 
     * @param topicId The ID of the grammar topic
     * @param userId The ID of the user
     * @return APIResponse containing topic progress details
     */
    @GetMapping("/topics/{topicId}/progress/{userId}")
    public APIResponse<GrammarStatsDTO.GrammarTopicProgressDTO> getTopicProgressById(
            @PathVariable Long topicId,
            @PathVariable Long userId) {
        log.info("Fetching grammar topic progress for topicId: {} and userId: {}", topicId, userId);
        
        GrammarStatsDTO.GrammarTopicProgressDTO topicProgress = grammarStatsService.getTopicProgressById(topicId, userId);
        
        return APIResponse.<GrammarStatsDTO.GrammarTopicProgressDTO>builder()
                .code(1000)
                .result(topicProgress)
                .build();
    }
}


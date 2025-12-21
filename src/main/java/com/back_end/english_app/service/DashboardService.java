package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.dashboard.DashboardSummaryDTO;
import com.back_end.english_app.dto.respones.dashboard.VocabStatsDTO;
import com.back_end.english_app.dto.respones.grammar.GrammarStatsDTO;
import com.back_end.english_app.repository.ForumPostRepository;
import com.back_end.english_app.repository.WritingPromptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final VocabStatsService vocabStatsService;
    private final GrammarStatsService grammarStatsService;
    private final ForumPostRepository forumPostRepository;
    private final WritingPromptRepository writingPromptRepository;

    public DashboardSummaryDTO getDashboardSummary(Long userId) {
        log.info("Getting dashboard summary for userId: {}", userId);

        // Lấy thống kê từ vựng
        DashboardSummaryDTO.ModuleStatsDTO vocabStats = null;
        try {
            VocabStatsDTO vocabData = vocabStatsService.getUserVocabStats(userId);
            Integer totalWords = vocabData.getTopicProgress().stream()
                    .mapToInt(VocabStatsDTO.TopicProgressDTO::getTotalWords)
                    .sum();
            
            vocabStats = DashboardSummaryDTO.ModuleStatsDTO.builder()
                    .moduleName("vocabulary")
                    .completed(vocabData.getTotalWordsLearned())
                    .total(totalWords > 0 ? totalWords : 100)
                    .progressText(vocabData.getTotalWordsLearned() + "/" + (totalWords > 0 ? totalWords : 100))
                    .progressPercentage(totalWords > 0 ? 
                        (vocabData.getTotalWordsLearned() * 100.0 / totalWords) : 0.0)
                    .build();
        } catch (Exception e) {
            log.error("Error getting vocab stats: ", e);
            vocabStats = createEmptyModuleStats("vocabulary", "0/0");
        }

        // Lấy thống kê ngữ pháp
        DashboardSummaryDTO.ModuleStatsDTO grammarStats = null;
        try {
            GrammarStatsDTO grammarData = grammarStatsService.getUserGrammarStats(userId);
            Integer totalLessons = grammarData.getTopicProgress().stream()
                    .mapToInt(GrammarStatsDTO.GrammarTopicProgressDTO::getTotalLessons)
                    .sum();
            
            grammarStats = DashboardSummaryDTO.ModuleStatsDTO.builder()
                    .moduleName("grammar")
                    .completed(grammarData.getTotalLessonLearned())
                    .total(totalLessons > 0 ? totalLessons : 80)
                    .progressText(grammarData.getTotalLessonLearned() + "/" + (totalLessons > 0 ? totalLessons : 80))
                    .progressPercentage(totalLessons > 0 ? 
                        (grammarData.getTotalLessonLearned() * 100.0 / totalLessons) : 0.0)
                    .build();
        } catch (Exception e) {
            log.error("Error getting grammar stats: ", e);
            grammarStats = createEmptyModuleStats("grammar", "0/0");
        }

        // Lấy thống kê writing
        DashboardSummaryDTO.ModuleStatsDTO writingStats = null;
        try {
            Long writingCount = writingPromptRepository.countByUserIdAndIsCompletedTrue(userId);
            writingStats = DashboardSummaryDTO.ModuleStatsDTO.builder()
                    .moduleName("writing")
                    .completed(writingCount.intValue())
                    .total(null)
                    .progressText(writingCount + " bài")
                    .progressPercentage(null)
                    .build();
        } catch (Exception e) {
            log.error("Error getting writing stats: ", e);
            writingStats = createEmptyModuleStats("writing", "0 bài");
        }

        // Lấy thống kê forum
        DashboardSummaryDTO.ModuleStatsDTO forumStats = null;
        try {
            Long forumCount = (long) forumPostRepository.countByUserIdAndIsActiveTrue(userId);
            forumStats = DashboardSummaryDTO.ModuleStatsDTO.builder()
                    .moduleName("forum")
                    .completed(forumCount.intValue())
                    .total(null)
                    .progressText(forumCount + " bài")
                    .progressPercentage(null)
                    .build();
        } catch (Exception e) {
            log.error("Error getting forum stats: ", e);
            forumStats = createEmptyModuleStats("forum", "0 bài");
        }

        return DashboardSummaryDTO.builder()
                .vocabularyStats(vocabStats)
                .grammarStats(grammarStats)
                .writingStats(writingStats)
                .forumStats(forumStats)
                .build();
    }

    private DashboardSummaryDTO.ModuleStatsDTO createEmptyModuleStats(String moduleName, String progressText) {
        return DashboardSummaryDTO.ModuleStatsDTO.builder()
                .moduleName(moduleName)
                .completed(0)
                .total(0)
                .progressText(progressText)
                .progressPercentage(0.0)
                .build();
    }
}

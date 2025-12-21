package com.back_end.english_app.service;

import com.back_end.english_app.dto.request.vocab.CompleteWordRequest;
import com.back_end.english_app.dto.respones.vocab.VocabWordResponse;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.entity.VocabUserProgress;
import com.back_end.english_app.entity.VocabWordEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.repository.VocabUserProgressRepository;
import com.back_end.english_app.repository.VocabWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabWordService {

    private final VocabWordRepository vocabWordRepository;
    private final VocabUserProgressRepository vocabUserProgressRepository;
    private final UserRepository userRepository;
    private final VocabTopicRepository vocabTopicRepository;
    private final BadgeCheckService badgeCheckService;
    private final FileUploadService fileUploadService;
    private final UserDailyStatsService userDailyStatsService;

//     Lấy tất cả từ vựng theo topic với trạng thái hoàn thành của user

    public List<VocabWordResponse> getWordsByTopicWithProgress(Long topicId, Long userId) {
        // Lấy tất cả từ vựng của topic
        List<VocabWordEntity> words = vocabWordRepository.findByTopicIdAndIsActiveTrue(topicId);

        // Lấy progress của user trong topic này
        List<VocabUserProgress> progressList = vocabUserProgressRepository.findByUserIdAndTopicId(userId, topicId);

        // Tạo map để tra cứu nhanh trạng thái hoàn thành
        // ✅ FIX: Lọc bỏ các progress không có word (word_id = NULL) để tránh NullPointerException
        Map<Long, Boolean> completionMap = progressList.stream()
                .filter(progress -> progress.getWord() != null)  // ✅ Chỉ lấy progress có word
                .collect(Collectors.toMap(
                    progress -> progress.getWord().getId(),
                    VocabUserProgress::getIsCompleted,
                    (existing, replacement) -> existing // Nếu trùng, giữ giá trị đầu tiên
                ));

        // Map sang DTO
        return words.stream()
                .map(word -> VocabWordResponse.builder()
                        .id(word.getId())
                        .englishWord(word.getEnglishWord())
                        .vietnameseMeaning(word.getVietnameseMeaning())
                        .pronunciation(word.getPronunciation())
                        .audioUrl(fileUploadService.buildFullUrl("Word/" + word.getAudioUrl()))
                        .imageUrl(fileUploadService.buildFullUrl("Word/" + word.getImageUrl()))
                        .exampleSentence(word.getExampleSentence())
                        .exampleTranslation(word.getExampleTranslation())
                        .wordType(word.getWordType())
                        .xpReward(word.getXpReward())
                        .isCompleted(completionMap.getOrDefault(word.getId(), false))
                        .build())
                .collect(Collectors.toList());
    }


//     Đánh dấu từ vựng đã hoàn thành và cộng XP cho user

    @Transactional
    public VocabWordResponse completeWord(CompleteWordRequest request, Long userId) {
        // Kiểm tra user tồn tại
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        // Kiểm tra word tồn tại
        VocabWordEntity word = vocabWordRepository.findById(request.getWordId())
                .orElseThrow(() -> new RuntimeException("Từ vựng không tồn tại"));

        // Kiểm tra topic tồn tại
        VocabTopicEntity topic = vocabTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic không tồn tại"));

        // Tìm hoặc tạo mới progress
        VocabUserProgress progress = vocabUserProgressRepository
                .findByUserIdAndWordIdAndTopicId(userId, request.getWordId(), request.getTopicId())
                .orElse(new VocabUserProgress());

        // Nếu chưa hoàn thành, đánh dấu hoàn thành và cộng XP
        if (!progress.getIsCompleted()) {
            progress.setUser(user);
            progress.setWord(word);
            progress.setTopic(topic);
            progress.setType(VocabUserProgress.ProgressType.flashcard);
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());

            vocabUserProgressRepository.save(progress);

            // Cộng XP cho user
            user.setTotalXp(user.getTotalXp() + word.getXpReward());
            userRepository.save(user);
            
            // Cập nhật daily stats
            userDailyStatsService.recordVocabLearned(user, 1);
            userDailyStatsService.recordXpEarned(user, word.getXpReward());
            
            badgeCheckService.checkAndUpdateBadges(userId, "VOCABULARY");
        }

        // Trả về response
        return VocabWordResponse.builder()
                .id(word.getId())
                .englishWord(word.getEnglishWord())
                .vietnameseMeaning(word.getVietnameseMeaning())
                .pronunciation(word.getPronunciation())
                .audioUrl(fileUploadService.buildFullUrl("Word/" + word.getAudioUrl()))
                .imageUrl(fileUploadService.buildFullUrl("Word/" + word.getImageUrl()))
                .exampleSentence(word.getExampleSentence())
                .exampleTranslation(word.getExampleTranslation())
                .wordType(word.getWordType())
                .xpReward(word.getXpReward())
                .isCompleted(true)
                .build();
    }
}

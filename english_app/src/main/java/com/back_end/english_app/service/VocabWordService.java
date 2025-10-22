package com.back_end.english_app.service;

import com.back_end.english_app.dto.request.vocab.AdminVocabWordRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordUpdateRequest;
import com.back_end.english_app.dto.request.vocab.CompleteWordRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabWordResponse;
import com.back_end.english_app.dto.respones.vocab.VocabWordResponse;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.entity.VocabUserProgress;
import com.back_end.english_app.entity.VocabWordEntity;
import com.back_end.english_app.mapper.VocabWordMapper;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.repository.VocabUserProgressRepository;
import com.back_end.english_app.repository.VocabWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabWordService {

    private final VocabWordRepository vocabWordRepository;
    private final VocabUserProgressRepository vocabUserProgressRepository;
    private final UserRepository userRepository;
    private final VocabTopicRepository vocabTopicRepository;
    private final VocabWordMapper mapper;

//     Lấy tất cả từ vựng theo topic với trạng thái hoàn thành của user

    public List<VocabWordResponse> getWordsByTopicWithProgress(Long topicId, Long userId) {
        // Lấy tất cả từ vựng của topic
        List<VocabWordEntity> words = vocabWordRepository.findByTopicIdAndIsActiveTrue(topicId);

        // Lấy progress của user trong topic này
        List<VocabUserProgress> progressList = vocabUserProgressRepository.findByUserIdAndTopicId(userId, topicId);

        // Tạo map để tra cứu nhanh trạng thái hoàn thành
        Map<Long, Boolean> completionMap = progressList.stream()
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
                        .audioUrl(word.getAudioUrl())
                        .imageUrl(word.getImageUrl())
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
        }

        // Trả về response
        return VocabWordResponse.builder()
                .id(word.getId())
                .englishWord(word.getEnglishWord())
                .vietnameseMeaning(word.getVietnameseMeaning())
                .pronunciation(word.getPronunciation())
                .audioUrl(word.getAudioUrl())
                .imageUrl(word.getImageUrl())
                .exampleSentence(word.getExampleSentence())
                .exampleTranslation(word.getExampleTranslation())
                .wordType(word.getWordType())
                .xpReward(word.getXpReward())
                .isCompleted(true)
                .build();
    }
//admin (hien thi, them, sua, xoa, tim kiem)
    //get all word
    public Page<AdminVocabWordResponse> getAllWords(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("topicId").ascending().and(Sort.by("createdAt").ascending()));
        Page<VocabWordEntity> wordPage = vocabWordRepository.findAllByTopic_IsActiveTrue(pageable);
        return wordPage.map(mapper::toResponse);
    }

    //create word
    public AdminVocabWordResponse createWord(AdminVocabWordRequest request, MultipartFile audioUrl, MultipartFile imageUrl) throws Exception{
        //Luu audio vao thu muc uploads/audio
        String audioFileName = UUID.randomUUID() + "_" + audioUrl.getOriginalFilename();
        Path uploadDirAudio = Paths.get("uploads/audio");
        Path uploadDir = Paths.get("uploads");
        if(!Files.exists(uploadDirAudio)){
            Files.createDirectories(uploadDirAudio);
        }
        Path audioFilePath = uploadDirAudio.resolve(audioFileName);
        Files.copy(audioUrl.getInputStream(), audioFilePath, StandardCopyOption.REPLACE_EXISTING);

        //Luu image vao thu muc uploads
        String imageFileName = UUID.randomUUID() + "_" + imageUrl.getOriginalFilename();
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        Path imageFilePath = uploadDir.resolve(imageFileName);
        Files.copy(imageUrl.getInputStream(), imageFilePath, StandardCopyOption.REPLACE_EXISTING);

        //set audio_url, image_url cho entity
        VocabWordEntity newWord = mapper.toEntity(request);
        newWord.setAudioUrl(audioFilePath.toString());
        newWord.setImageUrl(imageFilePath.toString());

        //set topic cho entity
        VocabTopicEntity topic = vocabTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chủ đề với ID: " + request.getTopicId()));
        newWord.setTopic(topic);

        //luu entity vao db
        VocabWordEntity savedVocabWord = vocabWordRepository.save(newWord);
        return mapper.toResponse(savedVocabWord);
    }

    //update word
    public AdminVocabWordResponse updateWord(Long id, AdminVocabWordUpdateRequest request, MultipartFile imageUrl, MultipartFile audioUrl){
        VocabWordEntity existing = vocabWordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng với ID: " + id));

        // luu audio moi neu co
        if (audioUrl != null && !audioUrl.isEmpty()) {
            // Xóa audio cũ
            if (existing.getAudioUrl() != null) {
                Path oldAudioFile = Paths.get(existing.getAudioUrl());
                if (Files.exists(oldAudioFile)) {
                    try {
                        Files.delete(oldAudioFile);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa audio cũ: " + e.getMessage());
                    }
                }
            }
            // Lưu audio mới
            String audioFileName = UUID.randomUUID() + "_" + audioUrl.getOriginalFilename();
            Path uploadDirAudio = Paths.get("uploads/audio");
            if (!Files.exists(uploadDirAudio)) {
                try {
                    Files.createDirectories(uploadDirAudio);
                } catch (Exception e) {
                    throw new RuntimeException("Không thể tạo thư mục lưu trữ audio: " + e.getMessage());
                }
            }
            Path audioFilePath = uploadDirAudio.resolve(audioFileName);
            try {
                Files.copy(audioUrl.getInputStream(), audioFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new RuntimeException("Không thể lưu audio mới: " + e.getMessage());
            }
            existing.setAudioUrl(audioFilePath.toString());
        }
        // luu image moi neu co
        if (imageUrl != null && !imageUrl.isEmpty()) {
            // Xóa ảnh cũ
            if (existing.getImageUrl() != null) {
                Path oldImageFile = Paths.get(existing.getImageUrl());
                if (Files.exists(oldImageFile)) {
                    try {
                        Files.delete(oldImageFile);
                    } catch (Exception e) {
                        System.err.println("Không thể xóa ảnh cũ: " + e.getMessage());
                    }
                }
            }
            // Lưu ảnh mới
            String imageFileName = UUID.randomUUID() + "_" + imageUrl.getOriginalFilename();
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                try {
                    Files.createDirectories(uploadDir);
                } catch (Exception e) {
                    throw new RuntimeException("Không thể tạo thư mục lưu trữ ảnh: " + e.getMessage());
                }
            }
            Path imageFilePath = uploadDir.resolve(imageFileName);
            try {
                Files.copy(imageUrl.getInputStream(), imageFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                throw new RuntimeException("Không thể lưu ảnh mới: " + e.getMessage());
            }
            existing.setImageUrl(imageFilePath.toString());
        }

        //update thong tin tu request
        existing.setEnglishWord(request.getEnglishWord());
        existing.setVietnameseMeaning(request.getVietnameseMeaning());
        existing.setPronunciation(request.getPronunciation());
        existing.setExampleSentence(request.getExampleSentence());
        existing.setExampleTranslation(request.getExampleSentenceMeaning());
        existing.setWordType(request.getWordType());
        existing.setXpReward(request.getXpReward());

        VocabWordEntity updateWord = vocabWordRepository.save(existing);
        return mapper.toResponse(updateWord);
    }

    //xoa tu vung (soft delete)
    public void softDeleteWord(Long id) {
        VocabWordEntity word = vocabWordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy từ vựng với ID: " + id));
        word.setIsActive(false);
        vocabWordRepository.save(word);
    }
}


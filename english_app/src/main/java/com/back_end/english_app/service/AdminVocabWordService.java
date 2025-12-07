package com.back_end.english_app.service;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabTopicResponse;
import com.back_end.english_app.dto.respones.vocab.AdminVocabWordResponse;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.entity.VocabWordEntity;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.repository.VocabWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminVocabWordService {
    private final VocabWordRepository vocabWordRepository;
    private final VocabTopicRepository vocabTopicRepository;

    //get all words
    public APIResponse<Page<AdminVocabWordResponse>> getAllWords(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<VocabWordEntity> wordsPage = vocabWordRepository.findAll(pageable);

            Page<AdminVocabWordResponse> responsePage = wordsPage.map(word -> new AdminVocabWordResponse(
                    word.getId(),
                    word.getTopic().getId(),
                    word.getEnglishWord(),
                    word.getVietnameseMeaning(),
                    word.getPronunciation(),
                    word.getAudioUrl(),
                    word.getImageUrl(),
                    word.getExampleSentence(),
                    word.getExampleTranslation(),
                    word.getWordType(),
                    word.getXpReward(),
                    word.getIsActive(),
                    word.getCreatedAt()
            ));

            return APIResponse.success(responsePage);

        } catch (Exception e) {
            return APIResponse.error("Lỗi truy xuất cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // thêm từ mới
    public APIResponse<?> addNewVocabWord(AdminVocabWordRequest request, MultipartFile audioFile, MultipartFile imageFile) {
        try {
            // Kiểm tra topic tồn tại
            Optional<VocabTopicEntity> topicOpt = vocabTopicRepository.findById(request.getTopicId());
            if (topicOpt.isEmpty()) {
                return APIResponse.error("Topic không tồn tại");
            }

            // lỗi file null
            if ((audioFile == null || audioFile.isEmpty()) && (imageFile == null || imageFile.isEmpty())) {
                return APIResponse.error("File audio và image không được để trống");
            }

            VocabTopicEntity topic = topicOpt.get();

            VocabWordEntity newWord = new VocabWordEntity();
            newWord.setTopic(topic);
            newWord.setEnglishWord(request.getEnglishWord());
            newWord.setVietnameseMeaning(request.getVietnameseMeaning());
            newWord.setPronunciation(request.getPronunciation());
            newWord.setExampleSentence(request.getExampleSentence());
            newWord.setExampleTranslation(request.getExampleTranslation());
            newWord.setWordType(request.getWordType());
            newWord.setXpReward(request.getXpReward() != null ? request.getXpReward() : 5);

            // Xử lý file audio
            String audioFileName = UUID.randomUUID().toString() + "_" + audioFile.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/Word");
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
            Path audioPath = uploadDir.resolve(audioFileName);
            Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);
            newWord.setAudioUrl(audioFileName);

            // Xử lý file image
            String imageFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            if (!Files.exists(uploadDir)) Files.createDirectories(uploadDir);
            Path imagePath = uploadDir.resolve(imageFileName);
            Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            newWord.setImageUrl(imageFileName);

            // Lưu từ mới
            vocabWordRepository.save(newWord);

            // Tăng totalWords của topic lên 1
            topic.setTotalWords(topic.getTotalWords() + 1);
            vocabTopicRepository.save(topic);

            // Trả về response
            AdminVocabWordResponse response = new AdminVocabWordResponse(
                    newWord.getId(),
                    newWord.getTopic().getId(),
                    newWord.getEnglishWord(),
                    newWord.getVietnameseMeaning(),
                    newWord.getPronunciation(),
                    newWord.getAudioUrl(),
                    newWord.getImageUrl(),
                    newWord.getExampleSentence(),
                    newWord.getExampleTranslation(),
                    newWord.getWordType(),
                    newWord.getXpReward(),
                    newWord.getIsActive(),
                    newWord.getCreatedAt()
            );

            return APIResponse.success(response);

        } catch (IOException e) {
            return APIResponse.error("Lỗi xử lý file: " + e.getMessage());
        } catch (Exception e) {
            return APIResponse.error("Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }

    // sửa từ vựng
    public APIResponse<?> updateVocabWord(Long id, AdminVocabWordUpdateRequest request, MultipartFile audioFile, MultipartFile imageFile) {
        try {
            Optional<VocabWordEntity> wordOpt = vocabWordRepository.findById(id);
            if (wordOpt.isEmpty()) {
                return APIResponse.error("Từ vựng không tồn tại");
            }

            VocabWordEntity word = wordOpt.get();

            // Cập nhật thông tin từ
            if (request.getEnglishWord() != null) word.setEnglishWord(request.getEnglishWord());
            if (request.getVietnameseMeaning() != null) word.setVietnameseMeaning(request.getVietnameseMeaning());
            if (request.getPronunciation() != null) word.setPronunciation(request.getPronunciation());
            if (request.getExampleSentence() != null) word.setExampleSentence(request.getExampleSentence());
            if (request.getExampleTranslation() != null) word.setExampleTranslation(request.getExampleTranslation());
            if (request.getWordType() != null) word.setWordType(request.getWordType());
            if (request.getXpReward() != null) word.setXpReward(request.getXpReward());

            // Xử lý file audio mới (nếu có)
            if (audioFile != null && !audioFile.isEmpty()) {
                String audioFileName = UUID.randomUUID().toString() + "_" + audioFile.getOriginalFilename();
                Path audioDir = Paths.get("uploads/AudioWord");
                if (!Files.exists(audioDir)) Files.createDirectories(audioDir);
                Path audioPath = audioDir.resolve(audioFileName);
                Files.copy(audioFile.getInputStream(), audioPath, StandardCopyOption.REPLACE_EXISTING);
                word.setAudioUrl(audioFileName);
            }

            // Xử lý file image mới (nếu có)
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path imageDir = Paths.get("uploads/ImageWord");
                if (!Files.exists(imageDir)) Files.createDirectories(imageDir);
                Path imagePath = imageDir.resolve(imageFileName);
                Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
                word.setImageUrl(imageFileName);
            }

            vocabWordRepository.save(word);

            // Trả về response
            AdminVocabWordResponse response = new AdminVocabWordResponse(
                    word.getId(),
                    word.getTopic().getId(),
                    word.getEnglishWord(),
                    word.getVietnameseMeaning(),
                    word.getPronunciation(),
                    word.getAudioUrl(),
                    word.getImageUrl(),
                    word.getExampleSentence(),
                    word.getExampleTranslation(),
                    word.getWordType(),
                    word.getXpReward(),
                    word.getIsActive(),
                    word.getCreatedAt()
            );

            return APIResponse.success(response);

        } catch (IOException e) {
            return APIResponse.error("Lỗi xử lý file: " + e.getMessage());
        } catch (Exception e) {
            return APIResponse.error("Lỗi khi cập nhật dữ liệu: " + e.getMessage());
        }
    }

    //xóa hoặc khôi phục từ vựng
    public APIResponse<String> deleteOrRestoreVocabWord(Long wordId, String status) {
        Optional<VocabWordEntity> wordOpt = vocabWordRepository.findById(wordId);
        if (wordOpt.isEmpty()) {
            return APIResponse.error("Từ vựng không tồn tại");
        }

        VocabWordEntity word = wordOpt.get();

        status = status.trim().toLowerCase();

        switch (status) {
            case "delete":
                if (!word.getIsActive()) {
                    return APIResponse.error("Từ vựng đã bị vô hiệu hóa trước đó");
                }
                word.setIsActive(false);
                vocabWordRepository.save(word);

                return APIResponse.success("Vô hiệu hóa từ vựng thành công");

            case "restore":
                if (word.getIsActive()) {
                    return APIResponse.error("Từ vựng đang hoạt động, không cần khôi phục");
                }
                word.setIsActive(true);
                vocabWordRepository.save(word);

                return APIResponse.success("Khôi phục từ vựng thành công");

            default:
                return APIResponse.error("Trạng thái không hợp lệ!");
        }
    }

}

package com.back_end.english_app.service.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabWordUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabWordResponse;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.entity.VocabWordEntity;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.repository.VocabWordRepository;
import com.back_end.english_app.service.user.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminVocabWordService {
    private final VocabWordRepository vocabWordRepository;
    private final VocabTopicRepository vocabTopicRepository;
    private final FileUploadService fileUploadService;

    //get all words
    public APIResponse<Page<AdminVocabWordResponse>> getAllWords(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<VocabWordEntity> wordsPage = vocabWordRepository.findAll(pageable);

            Page<AdminVocabWordResponse> responsePage = wordsPage.map(this::convertToResponse);

            return APIResponse.success(responsePage);

        } catch (Exception e) {
            return APIResponse.error("Lỗi truy xuất cơ sở dữ liệu: " + e.getMessage());
        }
    }

    //get words by topic
    public APIResponse<Page<AdminVocabWordResponse>> getWordsByTopic(Long topicId, int page, int size) {
        try {
            // Kiểm tra topic tồn tại
            Optional<VocabTopicEntity> topicOpt = vocabTopicRepository.findById(topicId);
            if (topicOpt.isEmpty()) {
                return APIResponse.error("Topic không tồn tại");
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<VocabWordEntity> wordsPage = vocabWordRepository.findByTopicId(topicId, pageable);

            Page<AdminVocabWordResponse> responsePage = wordsPage.map(this::convertToResponse);

            return APIResponse.success(responsePage);

        } catch (Exception e) {
            return APIResponse.error("Lỗi truy xuất cơ sở dữ liệu: " + e.getMessage());
        }
    }

    // thêm từ mới
    public APIResponse<?> addNewVocabWord(AdminVocabWordRequest request) {
        try {
            // Kiểm tra topic tồn tại
            Optional<VocabTopicEntity> topicOpt = vocabTopicRepository.findById(request.getTopicId());
            if (topicOpt.isEmpty()) {
                return APIResponse.error("Topic không tồn tại");
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

            // Lưu từ mới
            vocabWordRepository.save(newWord);

            // Tăng totalWords của topic lên 1
            topic.setTotalWords(topic.getTotalWords() + 1);
            vocabTopicRepository.save(topic);

            // Trả về response
            AdminVocabWordResponse response = convertToResponse(newWord);

            return APIResponse.success(response);

        } catch (Exception e) {
            return APIResponse.error("Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }

    // sửa từ vựng
    public APIResponse<?> updateVocabWord(Long id, AdminVocabWordUpdateRequest request) {
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

            vocabWordRepository.save(word);

            // Trả về response
            AdminVocabWordResponse response = convertToResponse(word);

            return APIResponse.success(response);

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

    // Helper method to convert VocabWordEntity to AdminVocabWordResponse
    private AdminVocabWordResponse convertToResponse(VocabWordEntity word) {
        return new AdminVocabWordResponse(
                word.getId(),
                word.getTopic().getId(),
                word.getEnglishWord(),
                word.getVietnameseMeaning(),
                word.getPronunciation(),
                null, // audioUrl - không cần nữa
                null, // imageUrl - không cần nữa
                word.getExampleSentence(),
                word.getExampleTranslation(),
                word.getWordType(),
                word.getXpReward(),
                word.getIsActive(),
                word.getCreatedAt()
        );
    }

}

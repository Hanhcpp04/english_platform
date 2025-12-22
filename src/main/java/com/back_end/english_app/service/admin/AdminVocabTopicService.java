package com.back_end.english_app.service.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabTopicResponse;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.repository.VocabTopicRepository;
import com.back_end.english_app.service.user.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminVocabTopicService {
    private final VocabTopicRepository vocabTopicRepository;
    private final FileUploadService fileUploadService;

    // get all vocab topic
    public APIResponse<Page<AdminVocabTopicResponse>> getAllTopics(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<VocabTopicEntity> topicsPage = vocabTopicRepository.findAll(pageable);

            Page<AdminVocabTopicResponse> responsePage = topicsPage.map(topic -> new AdminVocabTopicResponse(
                    topic.getId(),
                    topic.getEnglishName(),
                    topic.getName(),
                    topic.getDescription(),
                    topic.getIcon_url(), // Trả về icon name thay vì URL
                    topic.getXpReward(),
                    topic.getTotalWords(),
                    topic.getIsActive(),
                    topic.getCreatedAt(),
                    topic.getUpdatedAt()
            ));

            return APIResponse.success(responsePage);

        } catch (Exception e) {
            return APIResponse.error("Lỗi truy xuất cơ sở dữ liệu: " + e.getMessage());
        }
    }

    //thêm vocab topic mới
    public APIResponse<?> addNewVocabTopic(AdminVocabTopicRequest request){
        Optional<VocabTopicEntity> existTopic = vocabTopicRepository.findByEnglishName(request.getEnglishName());
        if(existTopic.isPresent()){
            return APIResponse.error("Tên topic đã tồn tại");
        }

        try{
            VocabTopicEntity newTopic = new VocabTopicEntity();

            newTopic.setName(request.getName());
            newTopic.setEnglishName(request.getEnglishName());
            newTopic.setDescription(request.getDescription());
            newTopic.setXpReward(request.getXpReward());
            newTopic.setIcon_url(request.getIcon()); // Lưu icon name

            vocabTopicRepository.save(newTopic);

            return APIResponse.success(
                    AdminVocabTopicResponse.builder()
                            .id(newTopic.getId())
                            .englishName(newTopic.getEnglishName())
                            .name(newTopic.getName())
                            .description(newTopic.getDescription())
                            .iconUrl(newTopic.getIcon_url())
                            .xpReward(newTopic.getXpReward())
                            .totalWords(newTopic.getTotalWords())
                            .isActive(newTopic.getIsActive())
                            .createdAt(newTopic.getCreatedAt())
                            .updatedAt(newTopic.getUpdatedAt())
                            .build()
            );
        } catch (Exception e) {
            return APIResponse.error("Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }

    public APIResponse<?> updateVocabTopic(Long id, AdminVocabTopicRequest request){
        Optional<VocabTopicEntity> optVocabTopic = vocabTopicRepository.findById(id);
        if(optVocabTopic.isEmpty()){
            return APIResponse.error("Vocab topic không tồn tại");
        }

        VocabTopicEntity topic = optVocabTopic.get();
        // Kiểm tra trùng tên nếu user truyền name mới
        if (request.getEnglishName() != null && !request.getEnglishName().equals(topic.getEnglishName())) {
            Optional<VocabTopicEntity> topicByName = vocabTopicRepository.findByEnglishName(request.getEnglishName());
            if (topicByName.isPresent()) {
                return APIResponse.error("Tên topic đã tồn tại, vui lòng nhập tên khác");
            }
        }

        try{
            if (request.getEnglishName() != null) topic.setEnglishName(request.getEnglishName());
            if (request.getName() != null) topic.setName(request.getName());
            if (request.getDescription() != null) topic.setDescription(request.getDescription());
            if (request.getXpReward() != null) topic.setXpReward(request.getXpReward());
            if (request.getIcon() != null) topic.setIcon_url(request.getIcon());

            vocabTopicRepository.save(topic);

            return APIResponse.success(
                    AdminVocabTopicResponse.builder()
                            .id(topic.getId())
                            .englishName(topic.getEnglishName())
                            .name(topic.getName())
                            .description(topic.getDescription())
                            .iconUrl(topic.getIcon_url())
                            .xpReward(topic.getXpReward())
                            .totalWords(topic.getTotalWords())
                            .isActive(topic.getIsActive())
                            .createdAt(topic.getCreatedAt())
                            .updatedAt(topic.getUpdatedAt())
                            .build()
            );
        } catch (Exception e) {
            return APIResponse.error("Lỗi lưu dữ liệu: " + e.getMessage());
        }
    }

    //xóa hoặc khôi phục vocab topic
    public APIResponse<String> deleteOrRestoreVocabTopic(Long id, String status) {
        // Tìm topic theo id
        Optional<VocabTopicEntity> optTopic = vocabTopicRepository.findById(id);
        if (optTopic.isEmpty()) {
            return APIResponse.error("Topic không tồn tại");
        }
        VocabTopicEntity topic = optTopic.get();
        status = status.trim().toLowerCase();

        switch (status) {
            case "delete":
                if (!topic.getIsActive()) {
                    return APIResponse.error("Topic đã bị vô hiệu hóa trước đó");
                }
                topic.setIsActive(false);
                vocabTopicRepository.save(topic);
                return APIResponse.success("Vô hiệu hóa Topic thành công");

            case "restore":
                if (topic.getIsActive()) {
                    return APIResponse.error("Topic đang hoạt động, không cần khôi phục");
                }
                topic.setIsActive(true);
                vocabTopicRepository.save(topic);
                return APIResponse.success("Khôi phục Topic thành công");

            default:
                return APIResponse.error("Trạng thái không hợp lệ!");
        }
    }
}

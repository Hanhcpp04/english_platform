package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.grammar.AdminGrammarTopicRequest;
import com.back_end.english_app.dto.respones.grammar.AdminGrammarTopicResponse;
import com.back_end.english_app.entity.GrammarTopicEntity;
import com.back_end.english_app.repository.GrammarTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminGrammarTopicService {

    private final GrammarTopicRepository grammarTopicRepository;

    /**
     * Lấy danh sách tất cả Grammar Topics với phân trang
     */
    public Page<AdminGrammarTopicResponse> getAllTopics(Pageable pageable) {
        Page<GrammarTopicEntity> topics = grammarTopicRepository.findAll(pageable);
        return topics.map(this::convertToResponse);
    }

    /**
     * Tạo Grammar Topic mới
     */
    @Transactional
    public AdminGrammarTopicResponse createTopic(AdminGrammarTopicRequest request) {
        GrammarTopicEntity topic = new GrammarTopicEntity();
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        topic.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        topic.setXpReward(request.getXpReward() != null ? request.getXpReward() : 100);
        
        GrammarTopicEntity savedTopic = grammarTopicRepository.save(topic);
        return convertToResponse(savedTopic);
    }

    /**
     * Cập nhật Grammar Topic
     */
    @Transactional
    public AdminGrammarTopicResponse updateTopic(Long id, AdminGrammarTopicRequest request) {
        GrammarTopicEntity topic = grammarTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));
        
        topic.setName(request.getName());
        topic.setDescription(request.getDescription());
        if (request.getIsActive() != null) {
            topic.setIsActive(request.getIsActive());
        }
        if (request.getXpReward() != null) {
            topic.setXpReward(request.getXpReward());
        }
        
        GrammarTopicEntity updatedTopic = grammarTopicRepository.save(topic);
        return convertToResponse(updatedTopic);
    }

    /**
     * Xóa hoặc khôi phục Grammar Topic
     */
    @Transactional
    public String deleteOrRestoreTopic(Long id, String status) {
        GrammarTopicEntity topic = grammarTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + id));
        
        if ("delete".equalsIgnoreCase(status)) {
            topic.setIsActive(false);
            grammarTopicRepository.save(topic);
            return "Topic deleted successfully";
        } else if ("restore".equalsIgnoreCase(status)) {
            topic.setIsActive(true);
            grammarTopicRepository.save(topic);
            return "Topic restored successfully";
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    /**
     * Convert Entity to Response DTO
     */
    private AdminGrammarTopicResponse convertToResponse(GrammarTopicEntity topic) {
        return AdminGrammarTopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .description(topic.getDescription())
                .isActive(topic.getIsActive())
                .xpReward(topic.getXpReward())
                .totalLessons(topic.getLessons() != null ? topic.getLessons().size() : 0)
                .totalExercises(topic.getExerciseTypes() != null ? topic.getExerciseTypes().size() : 0)
                .createdAt(topic.getCreatedAt())
                .build();
    }
}

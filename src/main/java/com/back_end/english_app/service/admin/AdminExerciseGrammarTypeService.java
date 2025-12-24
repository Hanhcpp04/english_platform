package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.grammar.AdminExerciseGrammarTypeRequest;
import com.back_end.english_app.dto.respones.grammar.AdminExerciseGrammarTypeResponse;
import com.back_end.english_app.entity.ExerciseGrammarTypeEntity;
import com.back_end.english_app.entity.GrammarTopicEntity;
import com.back_end.english_app.repository.ExerciseGrammarTypeRepository;
import com.back_end.english_app.repository.GrammarQuestionRepository;
import com.back_end.english_app.repository.GrammarTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminExerciseGrammarTypeService {

    private final ExerciseGrammarTypeRepository exerciseGrammarTypeRepository;
    private final GrammarTopicRepository grammarTopicRepository;
    private final GrammarQuestionRepository grammarQuestionRepository;

    /**
     * Lấy tất cả loại bài tập ngữ pháp
     */
    public Page<AdminExerciseGrammarTypeResponse> getAllExerciseTypes(Pageable pageable) {
        Page<ExerciseGrammarTypeEntity> types = exerciseGrammarTypeRepository.findAll(pageable);
        return types.map(this::convertToResponse);
    }

    /**
     * Lấy loại bài tập theo topic
     */
    public Page<AdminExerciseGrammarTypeResponse> getExerciseTypesByTopic(Long topicId, Pageable pageable) {
        Page<ExerciseGrammarTypeEntity> types = exerciseGrammarTypeRepository.findByTopic_Id(topicId, pageable);
        return types.map(this::convertToResponse);
    }

    /**
     * Tạo mới loại bài tập ngữ pháp
     */
    @Transactional
    public AdminExerciseGrammarTypeResponse createExerciseType(AdminExerciseGrammarTypeRequest request) {
        GrammarTopicEntity topic = grammarTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));

        ExerciseGrammarTypeEntity type = new ExerciseGrammarTypeEntity();
        type.setTopic(topic);
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive());

        ExerciseGrammarTypeEntity saved = exerciseGrammarTypeRepository.save(type);
        return convertToResponse(saved);
    }

    /**
     * Cập nhật loại bài tập ngữ pháp
     */
    @Transactional
    public AdminExerciseGrammarTypeResponse updateExerciseType(Long id, AdminExerciseGrammarTypeRequest request) {
        ExerciseGrammarTypeEntity type = exerciseGrammarTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + id));

        GrammarTopicEntity topic = grammarTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));

        type.setTopic(topic);
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setIsActive(request.getIsActive());

        ExerciseGrammarTypeEntity updated = exerciseGrammarTypeRepository.save(type);
        return convertToResponse(updated);
    }

    /**
     * Xóa hoặc khôi phục loại bài tập ngữ pháp
     */
    @Transactional
    public AdminExerciseGrammarTypeResponse toggleExerciseTypeStatus(Long id, String status) {
        ExerciseGrammarTypeEntity type = exerciseGrammarTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + id));

        if ("delete".equalsIgnoreCase(status)) {
            type.setIsActive(false);
        } else if ("restore".equalsIgnoreCase(status)) {
            type.setIsActive(true);
        } else {
            throw new RuntimeException("Invalid status: " + status + ". Expected 'delete' or 'restore'");
        }

        ExerciseGrammarTypeEntity updated = exerciseGrammarTypeRepository.save(type);
        return convertToResponse(updated);
    }

    private AdminExerciseGrammarTypeResponse convertToResponse(ExerciseGrammarTypeEntity entity) {
        Integer totalQuestions = grammarQuestionRepository.countByType_Id(entity.getId());
        
        return AdminExerciseGrammarTypeResponse.builder()
                .id(entity.getId())
                .topicId(entity.getTopic().getId())
                .topicName(entity.getTopic().getName())
                .name(entity.getName())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .totalQuestions(totalQuestions)
                .build();
    }
}

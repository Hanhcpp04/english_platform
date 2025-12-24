package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.vocab.AdminVocabExerciseTypeRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabExerciseTypeResponse;
import com.back_end.english_app.entity.VocabExerciseTypeEntity;
import com.back_end.english_app.repository.VocabExerciseQuestionRepository;
import com.back_end.english_app.repository.VocabExerciseTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminVocabExerciseTypeService {

    private final VocabExerciseTypeRepository vocabExerciseTypeRepository;
    private final VocabExerciseQuestionRepository vocabExerciseQuestionRepository;

    /**
     * Lấy tất cả loại bài tập từ vựng
     */
    public Page<AdminVocabExerciseTypeResponse> getAllExerciseTypes(Pageable pageable) {
        Page<VocabExerciseTypeEntity> types = vocabExerciseTypeRepository.findAll(pageable);
        return types.map(this::convertToResponse);
    }

    /**
     * Tạo mới loại bài tập từ vựng
     */
    @Transactional
    public AdminVocabExerciseTypeResponse createExerciseType(AdminVocabExerciseTypeRequest request) {
        VocabExerciseTypeEntity type = new VocabExerciseTypeEntity();
        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setInstruction(request.getInstruction());
        type.setIsActive(request.getIsActive());

        VocabExerciseTypeEntity saved = vocabExerciseTypeRepository.save(type);
        return convertToResponse(saved);
    }

    /**
     * Cập nhật loại bài tập từ vựng
     */
    @Transactional
    public AdminVocabExerciseTypeResponse updateExerciseType(Long id, AdminVocabExerciseTypeRequest request) {
        VocabExerciseTypeEntity type = vocabExerciseTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + id));

        type.setName(request.getName());
        type.setDescription(request.getDescription());
        type.setInstruction(request.getInstruction());
        type.setIsActive(request.getIsActive());

        VocabExerciseTypeEntity updated = vocabExerciseTypeRepository.save(type);
        return convertToResponse(updated);
    }

    /**
     * Xóa hoặc khôi phục loại bài tập từ vựng
     */
    @Transactional
    public AdminVocabExerciseTypeResponse toggleExerciseTypeStatus(Long id, String status) {
        VocabExerciseTypeEntity type = vocabExerciseTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + id));

        if ("delete".equalsIgnoreCase(status)) {
            type.setIsActive(false);
        } else if ("restore".equalsIgnoreCase(status)) {
            type.setIsActive(true);
        } else {
            throw new RuntimeException("Invalid status: " + status + ". Expected 'delete' or 'restore'");
        }

        VocabExerciseTypeEntity updated = vocabExerciseTypeRepository.save(type);
        return convertToResponse(updated);
    }

    private AdminVocabExerciseTypeResponse convertToResponse(VocabExerciseTypeEntity entity) {
        Integer totalQuestions = vocabExerciseQuestionRepository.countByType_Id(entity.getId());
        
        return AdminVocabExerciseTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .instruction(entity.getInstruction())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .totalQuestions(totalQuestions)
                .build();
    }
}

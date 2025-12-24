package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.vocab.AdminVocabExerciseQuestionRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabExerciseQuestionResponse;
import com.back_end.english_app.entity.VocabExerciseQuestionEntity;
import com.back_end.english_app.entity.VocabExerciseTypeEntity;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.repository.VocabExerciseQuestionRepository;
import com.back_end.english_app.repository.VocabExerciseTypeRepository;
import com.back_end.english_app.repository.VocabTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminVocabExerciseQuestionService {

    private final VocabExerciseQuestionRepository vocabExerciseQuestionRepository;
    private final VocabExerciseTypeRepository vocabExerciseTypeRepository;
    private final VocabTopicRepository vocabTopicRepository;

    /**
     * Lấy tất cả câu hỏi bài tập từ vựng
     */
    public Page<AdminVocabExerciseQuestionResponse> getAllQuestions(Pageable pageable) {
        Page<VocabExerciseQuestionEntity> questions = vocabExerciseQuestionRepository.findAll(pageable);
        return questions.map(this::convertToResponse);
    }

    /**
     * Lấy câu hỏi theo loại bài tập
     */
    public Page<AdminVocabExerciseQuestionResponse> getQuestionsByType(Long typeId, Pageable pageable) {
        Page<VocabExerciseQuestionEntity> questions = vocabExerciseQuestionRepository.findByType_Id(typeId, pageable);
        return questions.map(this::convertToResponse);
    }

    /**
     * Lấy câu hỏi theo topic
     */
    public Page<AdminVocabExerciseQuestionResponse> getQuestionsByTopic(Long topicId, Pageable pageable) {
        Page<VocabExerciseQuestionEntity> questions = vocabExerciseQuestionRepository.findByTopic_Id(topicId, pageable);
        return questions.map(this::convertToResponse);
    }

    /**
     * Tạo mới câu hỏi bài tập từ vựng
     */
    @Transactional
    public AdminVocabExerciseQuestionResponse createQuestion(AdminVocabExerciseQuestionRequest request) {
        VocabExerciseTypeEntity type = vocabExerciseTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + request.getTypeId()));

        VocabTopicEntity topic = null;
        if (request.getTopicId() != null) {
            topic = vocabTopicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));
        }

        VocabExerciseQuestionEntity question = new VocabExerciseQuestionEntity();
        question.setType(type);
        question.setTopic(topic);
        question.setQuestion(request.getQuestion());
        question.setOptions(request.getOptions());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setExplanation(request.getExplanation());
        question.setXpReward(request.getXpReward());
        question.setIsActive(request.getIsActive());

        VocabExerciseQuestionEntity saved = vocabExerciseQuestionRepository.save(question);
        return convertToResponse(saved);
    }

    /**
     * Cập nhật câu hỏi bài tập từ vựng
     */
    @Transactional
    public AdminVocabExerciseQuestionResponse updateQuestion(Long id, AdminVocabExerciseQuestionRequest request) {
        VocabExerciseQuestionEntity question = vocabExerciseQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        VocabExerciseTypeEntity type = vocabExerciseTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + request.getTypeId()));

        VocabTopicEntity topic = null;
        if (request.getTopicId() != null) {
            topic = vocabTopicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));
        }

        question.setType(type);
        question.setTopic(topic);
        question.setQuestion(request.getQuestion());
        question.setOptions(request.getOptions());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setExplanation(request.getExplanation());
        question.setXpReward(request.getXpReward());
        question.setIsActive(request.getIsActive());

        VocabExerciseQuestionEntity updated = vocabExerciseQuestionRepository.save(question);
        return convertToResponse(updated);
    }

    /**
     * Xóa hoặc khôi phục câu hỏi
     */
    @Transactional
    public AdminVocabExerciseQuestionResponse toggleQuestionStatus(Long id, String status) {
        VocabExerciseQuestionEntity question = vocabExerciseQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        if ("delete".equalsIgnoreCase(status)) {
            question.setIsActive(false);
        } else if ("restore".equalsIgnoreCase(status)) {
            question.setIsActive(true);
        } else {
            throw new RuntimeException("Invalid status: " + status + ". Expected 'delete' or 'restore'");
        }

        VocabExerciseQuestionEntity updated = vocabExerciseQuestionRepository.save(question);
        return convertToResponse(updated);
    }

    private AdminVocabExerciseQuestionResponse convertToResponse(VocabExerciseQuestionEntity entity) {
        return AdminVocabExerciseQuestionResponse.builder()
                .id(entity.getId())
                .typeId(entity.getType().getId())
                .typeName(entity.getType().getName())
                .topicId(entity.getTopic() != null ? entity.getTopic().getId() : null)
                .topicName(entity.getTopic() != null ? entity.getTopic().getName() : null)
                .question(entity.getQuestion())
                .options(entity.getOptions())
                .correctAnswer(entity.getCorrectAnswer())
                .explanation(entity.getExplanation())
                .xpReward(entity.getXpReward())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

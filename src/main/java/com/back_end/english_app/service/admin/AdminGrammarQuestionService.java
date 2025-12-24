package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.grammar.AdminGrammarQuestionRequest;
import com.back_end.english_app.dto.respones.grammar.AdminGrammarQuestionResponse;
import com.back_end.english_app.entity.ExerciseGrammarTypeEntity;
import com.back_end.english_app.entity.GrammarLessonEntity;
import com.back_end.english_app.entity.GrammarQuestionEntity;
import com.back_end.english_app.repository.ExerciseGrammarTypeRepository;
import com.back_end.english_app.repository.GrammarLessonRepository;
import com.back_end.english_app.repository.GrammarQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminGrammarQuestionService {

    private final GrammarQuestionRepository grammarQuestionRepository;
    private final ExerciseGrammarTypeRepository exerciseGrammarTypeRepository;
    private final GrammarLessonRepository grammarLessonRepository;

    /**
     * Lấy tất cả câu hỏi bài tập ngữ pháp
     */
    public Page<AdminGrammarQuestionResponse> getAllQuestions(Pageable pageable) {
        Page<GrammarQuestionEntity> questions = grammarQuestionRepository.findAll(pageable);
        return questions.map(this::convertToResponse);
    }

    /**
     * Lấy câu hỏi theo loại bài tập
     */
    public Page<AdminGrammarQuestionResponse> getQuestionsByType(Long typeId, Pageable pageable) {
        Page<GrammarQuestionEntity> questions = grammarQuestionRepository.findByType_Id(typeId, pageable);
        return questions.map(this::convertToResponse);
    }

    /**
     * Lấy câu hỏi theo lesson
     */
    public Page<AdminGrammarQuestionResponse> getQuestionsByLesson(Long lessonId, Pageable pageable) {
        Page<GrammarQuestionEntity> questions = grammarQuestionRepository.findByLesson_Id(lessonId, pageable);
        return questions.map(this::convertToResponse);
    }

    /**
     * Tạo mới câu hỏi bài tập ngữ pháp
     */
    @Transactional
    public AdminGrammarQuestionResponse createQuestion(AdminGrammarQuestionRequest request) {
        GrammarLessonEntity lesson = grammarLessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + request.getLessonId()));

        ExerciseGrammarTypeEntity type = exerciseGrammarTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + request.getTypeId()));

        GrammarQuestionEntity question = new GrammarQuestionEntity();
        question.setLesson(lesson);
        question.setType(type);
        question.setQuestion(request.getQuestion());
        question.setOptions(request.getOptions());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setXpReward(request.getXpReward());
        question.setIsActive(request.getIsActive());

        GrammarQuestionEntity saved = grammarQuestionRepository.save(question);
        return convertToResponse(saved);
    }

    /**
     * Cập nhật câu hỏi bài tập ngữ pháp
     */
    @Transactional
    public AdminGrammarQuestionResponse updateQuestion(Long id, AdminGrammarQuestionRequest request) {
        GrammarQuestionEntity question = grammarQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        GrammarLessonEntity lesson = grammarLessonRepository.findById(request.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + request.getLessonId()));

        ExerciseGrammarTypeEntity type = exerciseGrammarTypeRepository.findById(request.getTypeId())
                .orElseThrow(() -> new RuntimeException("Exercise type not found with id: " + request.getTypeId()));

        question.setLesson(lesson);
        question.setType(type);
        question.setQuestion(request.getQuestion());
        question.setOptions(request.getOptions());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setXpReward(request.getXpReward());
        question.setIsActive(request.getIsActive());

        GrammarQuestionEntity updated = grammarQuestionRepository.save(question);
        return convertToResponse(updated);
    }

    /**
     * Xóa hoặc khôi phục câu hỏi
     */
    @Transactional
    public AdminGrammarQuestionResponse toggleQuestionStatus(Long id, String status) {
        GrammarQuestionEntity question = grammarQuestionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        if ("delete".equalsIgnoreCase(status)) {
            question.setIsActive(false);
        } else if ("restore".equalsIgnoreCase(status)) {
            question.setIsActive(true);
        } else {
            throw new RuntimeException("Invalid status: " + status + ". Expected 'delete' or 'restore'");
        }

        GrammarQuestionEntity updated = grammarQuestionRepository.save(question);
        return convertToResponse(updated);
    }

    private AdminGrammarQuestionResponse convertToResponse(GrammarQuestionEntity entity) {
        return AdminGrammarQuestionResponse.builder()
                .id(entity.getId())
                .lessonId(entity.getLesson().getId())
                .lessonTitle(entity.getLesson().getTitle())
                .typeId(entity.getType().getId())
                .typeName(entity.getType().getName())
                .question(entity.getQuestion())
                .options(entity.getOptions())
                .correctAnswer(entity.getCorrectAnswer())
                .xpReward(entity.getXpReward())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

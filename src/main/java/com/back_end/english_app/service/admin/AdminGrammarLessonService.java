package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.grammar.AdminGrammarLessonRequest;
import com.back_end.english_app.dto.respones.grammar.AdminGrammarLessonResponse;
import com.back_end.english_app.entity.GrammarLessonEntity;
import com.back_end.english_app.entity.GrammarTopicEntity;
import com.back_end.english_app.repository.GrammarLessonRepository;
import com.back_end.english_app.repository.GrammarTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminGrammarLessonService {

    private final GrammarLessonRepository grammarLessonRepository;
    private final GrammarTopicRepository grammarTopicRepository;

    /**
     * Lấy tất cả bài học với phân trang
     */
    public Page<AdminGrammarLessonResponse> getAllLessons(Pageable pageable) {
        Page<GrammarLessonEntity> lessons = grammarLessonRepository.findAll(pageable);
        return lessons.map(this::convertToResponse);
    }

    /**
     * Lấy bài học theo topic
     */
    public Page<AdminGrammarLessonResponse> getLessonsByTopic(Long topicId, Pageable pageable) {
        Page<GrammarLessonEntity> lessons = grammarLessonRepository.findByTopicId(topicId, pageable);
        return lessons.map(this::convertToResponse);
    }

    /**
     * Tạo bài học mới
     */
    @Transactional
    public AdminGrammarLessonResponse createLesson(AdminGrammarLessonRequest request) {
        GrammarTopicEntity topic = grammarTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));
        
        GrammarLessonEntity lesson = new GrammarLessonEntity();
        lesson.setTopic(topic);
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setXpReward(request.getXpReward() != null ? request.getXpReward() : 100);
        lesson.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        
        GrammarLessonEntity savedLesson = grammarLessonRepository.save(lesson);
        return convertToResponse(savedLesson);
    }

    /**
     * Cập nhật bài học
     */
    @Transactional
    public AdminGrammarLessonResponse updateLesson(Long id, AdminGrammarLessonRequest request) {
        GrammarLessonEntity lesson = grammarLessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        
        if (request.getTopicId() != null && !request.getTopicId().equals(lesson.getTopic().getId())) {
            GrammarTopicEntity newTopic = grammarTopicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + request.getTopicId()));
            lesson.setTopic(newTopic);
        }
        
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        if (request.getXpReward() != null) {
            lesson.setXpReward(request.getXpReward());
        }
        if (request.getIsActive() != null) {
            lesson.setIsActive(request.getIsActive());
        }
        
        GrammarLessonEntity updatedLesson = grammarLessonRepository.save(lesson);
        return convertToResponse(updatedLesson);
    }

    /**
     * Xóa hoặc khôi phục bài học
     */
    @Transactional
    public String deleteOrRestoreLesson(Long id, String status) {
        GrammarLessonEntity lesson = grammarLessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));
        
        if ("delete".equalsIgnoreCase(status)) {
            lesson.setIsActive(false);
            grammarLessonRepository.save(lesson);
            return "Lesson deleted successfully";
        } else if ("restore".equalsIgnoreCase(status)) {
            lesson.setIsActive(true);
            grammarLessonRepository.save(lesson);
            return "Lesson restored successfully";
        } else {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    /**
     * Convert Entity to Response DTO
     */
    private AdminGrammarLessonResponse convertToResponse(GrammarLessonEntity lesson) {
        return AdminGrammarLessonResponse.builder()
                .id(lesson.getId())
                .topicId(lesson.getTopic().getId())
                .topicName(lesson.getTopic().getName())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .xpReward(lesson.getXpReward())
                .isActive(lesson.getIsActive())
                .totalQuestions(lesson.getQuestions() != null ? lesson.getQuestions().size() : 0)
                .createdAt(lesson.getCreatedAt())
                .build();
    }
}

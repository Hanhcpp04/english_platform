package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.writing.AdminGradingCriteriaRequest;
import com.back_end.english_app.dto.request.writing.AdminWritingTaskRequest;
import com.back_end.english_app.dto.respones.writing.AdminGradingCriteriaResponse;
import com.back_end.english_app.dto.respones.writing.AdminWritingTaskResponse;
import com.back_end.english_app.entity.WritingGradingCriteriaEntity;
import com.back_end.english_app.entity.WritingTopicEntity;
import com.back_end.english_app.entity.WrittingTaskEntity;
import com.back_end.english_app.exception.BadRequestException;
import com.back_end.english_app.exception.ResourceNotFoundException;
import com.back_end.english_app.repository.WritingGradingCriteriaRepository;
import com.back_end.english_app.repository.WritingPromptRepository;
import com.back_end.english_app.repository.WritingTaskRepository;
import com.back_end.english_app.repository.WritingTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AdminWritingTaskService {

    private final WritingTaskRepository writingTaskRepository;
    private final WritingTopicRepository writingTopicRepository;
    private final WritingGradingCriteriaRepository gradingCriteriaRepository;
    private final WritingPromptRepository writingPromptRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Page<AdminWritingTaskResponse> getAllTasks(Pageable pageable) {
        return writingTaskRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    public Page<AdminWritingTaskResponse> getTasksByTopic(Integer topicId, Pageable pageable) {
        return writingTaskRepository.findByWritingTopicId(topicId, pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public AdminWritingTaskResponse createTask(AdminWritingTaskRequest request) {
        WritingTopicEntity topic = writingTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

        WrittingTaskEntity task = new WrittingTaskEntity();
        task.setWritingTopic(topic);
        task.setQuestion(request.getQuestion());
        task.setWritingTips(request.getWritingTips());
        task.setXpReward(request.getXpReward());
        task.setIsActive(request.getIsActive());
        
        WrittingTaskEntity saved = writingTaskRepository.save(task);
        
        // Tạo tiêu chí chấm điểm mặc định
        createDefaultGradingCriteria(saved);
        
        return convertToResponse(saved);
    }

    @Transactional
    public AdminWritingTaskResponse updateTask(Integer id, AdminWritingTaskRequest request) {
        WrittingTaskEntity task = writingTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        WritingTopicEntity topic = writingTopicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

        task.setWritingTopic(topic);
        task.setQuestion(request.getQuestion());
        task.setWritingTips(request.getWritingTips());
        task.setXpReward(request.getXpReward());
        task.setIsActive(request.getIsActive());
        
        WrittingTaskEntity updated = writingTaskRepository.save(task);
        return convertToResponse(updated);
    }

    @Transactional
    public void deleteOrRestoreTask(Integer id, String status) {
        WrittingTaskEntity task = writingTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        task.setIsActive("restore".equals(status));
        writingTaskRepository.save(task);
    }

    @Transactional
    public AdminGradingCriteriaResponse updateGradingCriteria(Integer taskId, AdminGradingCriteriaRequest request) {
        WrittingTaskEntity task = writingTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        WritingGradingCriteriaEntity criteria = gradingCriteriaRepository.findByTaskId(taskId)
                .orElseGet(() -> {
                    WritingGradingCriteriaEntity newCriteria = new WritingGradingCriteriaEntity();
                    newCriteria.setTask(task);
                    return newCriteria;
                });

        // Validate weights sum to 100
        int totalWeight = request.getGrammarWeight() + request.getVocabularyWeight() + request.getCoherenceWeight();
        if (totalWeight != 100) {
            throw new BadRequestException("Grading weights must sum to 100%. Current sum: " + totalWeight + "%");
        }

        criteria.setGrammarWeight(request.getGrammarWeight());
        criteria.setVocabularyWeight(request.getVocabularyWeight());
        criteria.setCoherenceWeight(request.getCoherenceWeight());
        criteria.setMinWordCount(request.getMinWordCount());
        criteria.setMaxWordCount(request.getMaxWordCount());
        criteria.setCustomInstructions(request.getCustomInstructions());

        WritingGradingCriteriaEntity saved = gradingCriteriaRepository.save(criteria);
        return convertGradingCriteriaToResponse(saved);
    }

    private void createDefaultGradingCriteria(WrittingTaskEntity task) {
        WritingGradingCriteriaEntity criteria = new WritingGradingCriteriaEntity();
        criteria.setTask(task);
        criteria.setGrammarWeight(30);
        criteria.setVocabularyWeight(30);
        criteria.setCoherenceWeight(40);
        criteria.setMinWordCount(100);
        criteria.setMaxWordCount(500);
        gradingCriteriaRepository.save(criteria);
    }

    private AdminWritingTaskResponse convertToResponse(WrittingTaskEntity task) {
        long submissionCount = writingPromptRepository.countByTaskId(task.getId());
        
        AdminGradingCriteriaResponse gradingCriteria = gradingCriteriaRepository
                .findByTaskId(task.getId())
                .map(this::convertGradingCriteriaToResponse)
                .orElse(null);

        return AdminWritingTaskResponse.builder()
                .id(task.getId())
                .topicId(task.getWritingTopic().getId())
                .topicName(task.getWritingTopic().getName())
                .question(task.getQuestion())
                .writingTips(task.getWritingTips())
                .xpReward(task.getXpReward())
                .isActive(task.getIsActive())
                .gradingCriteria(gradingCriteria)
                .totalSubmissions((int) submissionCount)
                .createdAt(task.getCreatedAt() != null ? task.getCreatedAt().format(formatter) : null)
                .build();
    }

    private AdminGradingCriteriaResponse convertGradingCriteriaToResponse(WritingGradingCriteriaEntity criteria) {
        return AdminGradingCriteriaResponse.builder()
                .id(criteria.getId())
                .taskId(criteria.getTask().getId())
                .grammarWeight(criteria.getGrammarWeight())
                .vocabularyWeight(criteria.getVocabularyWeight())
                .coherenceWeight(criteria.getCoherenceWeight())
                .minWordCount(criteria.getMinWordCount())
                .maxWordCount(criteria.getMaxWordCount())
                .customInstructions(criteria.getCustomInstructions())
                .updatedAt(criteria.getUpdatedAt() != null ? criteria.getUpdatedAt().format(formatter) : null)
                .build();
    }
}

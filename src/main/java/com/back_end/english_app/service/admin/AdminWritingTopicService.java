package com.back_end.english_app.service.admin;

import com.back_end.english_app.dto.request.writing.AdminWritingTopicRequest;
import com.back_end.english_app.dto.respones.writing.AdminWritingTopicResponse;
import com.back_end.english_app.entity.WritingTopicEntity;
import com.back_end.english_app.exception.ResourceNotFoundException;
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
public class AdminWritingTopicService {

    private final WritingTopicRepository writingTopicRepository;
    private final WritingTaskRepository writingTaskRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Page<AdminWritingTopicResponse> getAllTopics(Pageable pageable) {
        return writingTopicRepository.findAll(pageable)
                .map(this::convertToResponse);
    }

    @Transactional
    public AdminWritingTopicResponse createTopic(AdminWritingTopicRequest request) {
        WritingTopicEntity topic = new WritingTopicEntity();
        topic.setName(request.getName());
        topic.setIsActive(request.getIsActive());
        
        WritingTopicEntity saved = writingTopicRepository.save(topic);
        return convertToResponse(saved);
    }

    @Transactional
    public AdminWritingTopicResponse updateTopic(Integer id, AdminWritingTopicRequest request) {
        WritingTopicEntity topic = writingTopicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        
        topic.setName(request.getName());
        topic.setIsActive(request.getIsActive());
        
        WritingTopicEntity updated = writingTopicRepository.save(topic);
        return convertToResponse(updated);
    }

    @Transactional
    public void deleteOrRestoreTopic(Integer id, String status) {
        WritingTopicEntity topic = writingTopicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
        
        topic.setIsActive("restore".equals(status));
        writingTopicRepository.save(topic);
    }

    private AdminWritingTopicResponse convertToResponse(WritingTopicEntity topic) {
        long taskCount = writingTaskRepository.countByTopicId(topic.getId());
        
        return AdminWritingTopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .isActive(topic.getIsActive())
                .totalTasks((int) taskCount)
                .createdAt(topic.getCreatedAt() != null ? topic.getCreatedAt().format(formatter) : null)
                .build();
    }
}

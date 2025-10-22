package com.back_end.english_app.service;

import com.back_end.english_app.dto.request.vocab.AdminVocabTopicRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabTopicResponse;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.mapper.VocabTopicMapper;
import com.back_end.english_app.repository.VocabTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VocabTopicService {
    private final VocabTopicRepository vocabTopicRepository;
    private final VocabTopicMapper mapper;
//admin manage topic (hien thi, them, sua, xoa, tim kiem)
    //get all topic
    public List<AdminVocabTopicResponse> getAllTopics() {
        List<VocabTopicEntity> topics = vocabTopicRepository.findAllByIsActiveTrue();
        return topics.stream()
                .map(mapper::toResponse)
                .toList();
    }

    //add new topic
    public AdminVocabTopicResponse createTopic(AdminVocabTopicRequest request, MultipartFile iconUrl) throws Exception{
        //Luu anh vao thu muc uploads
        String fileName = UUID.randomUUID() + "_" + iconUrl.getOriginalFilename();
        Path uploadDir = Paths.get("uploads");
        if(!Files.exists(uploadDir)){
            Files.createDirectories(uploadDir);
        }
        Path filePath = uploadDir.resolve(fileName);
        Files.copy(iconUrl.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        //set icon_url cho entity
        VocabTopicEntity newVocab = mapper.toEntity(request);
        newVocab.setIcon_url(filePath.toString());

        //luu entity vao db
        VocabTopicEntity savedVocab = vocabTopicRepository.save(newVocab);
        return mapper.toResponse(savedVocab);
    }

    //update topic
    public AdminVocabTopicResponse updateTopic(Long id, AdminVocabTopicUpdateRequest request, MultipartFile iconUrl) throws IOException {
        // kiem tra topic co ton tai khong
        VocabTopicEntity existing = vocabTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chủ đề với ID: " + id)); //404

        // luu anh moi neu co
        if (iconUrl != null && !iconUrl.isEmpty()) {
            // Xóa ảnh cũ
            if (existing.getIcon_url() != null) {
                Path oldFile = Paths.get(existing.getIcon_url());
                if (Files.exists(oldFile)) {
                    try {
                        Files.delete(oldFile);
                    } catch (IOException e) {
                        System.err.println("Không thể xóa ảnh cũ: " + e.getMessage());
                    }
                }
            }

            // Lưu ảnh mới
            String fileName = UUID.randomUUID() + "_" + iconUrl.getOriginalFilename();
            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);

            try (InputStream inputStream = iconUrl.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            existing.setIcon_url("uploads/" + fileName);
        }

        if (request.getEnglishName() != null) existing.setEnglishName(request.getEnglishName());
        if (request.getName() != null) existing.setName(request.getName());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getXpReward() != null) existing.setXpReward(request.getXpReward());
        if (request.getIsActive() != null) existing.setIsActive(request.getIsActive());

        // luu db
        VocabTopicEntity updated = vocabTopicRepository.save(existing);

        // tra ve dto response
        return mapper.toResponse(updated);
    }


    //detele topic (soft delete)
    public void softDeleteTopic(Long id) {
        VocabTopicEntity topic = vocabTopicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Topic không tồn tại!"));
        topic.setIsActive(false);
        vocabTopicRepository.save(topic);
        vocabTopicRepository.deactivateByTopicId(id);
    }

    // search topic by name or englishName
    public List<AdminVocabTopicResponse> searchTopics(String keyword) {
        List<VocabTopicEntity> topics = vocabTopicRepository.findByName(keyword, keyword);
        return topics.stream()
                .map(mapper::toResponse)
                .toList();
    }
}

package com.back_end.english_app.mapper;

import com.back_end.english_app.dto.request.vocab.AdminVocabTopicRequest;
import com.back_end.english_app.dto.request.vocab.AdminVocabTopicUpdateRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabTopicResponse;
import com.back_end.english_app.entity.VocabTopicEntity;
import org.springframework.stereotype.Component;

@Component
public class VocabTopicMapper {
    public VocabTopicEntity toEntity(AdminVocabTopicRequest request) {
        VocabTopicEntity entity = new VocabTopicEntity();
        entity.setEnglishName(request.getEnglishName());
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setIcon_url(request.getIconUrl());
        entity.setXpReward(request.getXpReward());
        entity.setIsActive(true);
        entity.setTotalWords(0);
        return entity;
    }
    public AdminVocabTopicResponse toResponse(VocabTopicEntity entity) {
        if (entity == null) return null;

        return AdminVocabTopicResponse.builder()
                .id(entity.getId())
                .englishName(entity.getEnglishName())
                .name(entity.getName())
                .description(entity.getDescription())
                .iconUrl(entity.getIcon_url())
                .xpReward(entity.getXpReward())
                .totalWords(entity.getTotalWords())
                .createdAt(entity.getCreatedAt())
                .build();
    }
    public void updateEntity(VocabTopicEntity entity, AdminVocabTopicUpdateRequest request) {
        if (request.getEnglishName() != null) entity.setEnglishName(request.getEnglishName());
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getIconUrl() != null) entity.setIcon_url(request.getIconUrl());
        if (request.getXpReward() != null) entity.setXpReward(request.getXpReward());
        if (request.getIsActive() != null) entity.setIsActive(request.getIsActive());
    }
}

package com.back_end.english_app.mapper;

import com.back_end.english_app.dto.respones.dashboard.VocabTopicDTO;
import com.back_end.english_app.entity.VocabTopicEntity;

public class VocapTopicEntity {

    public VocabTopicDTO convertToDTO(VocabTopicEntity entity) {
        return VocabTopicDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .iconUrl(entity.getIcon_url())
                .xpReward(entity.getXpReward())
                .totalWords(entity.getTotalWords())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

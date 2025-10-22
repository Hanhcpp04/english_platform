package com.back_end.english_app.mapper;

import com.back_end.english_app.dto.request.vocab.AdminVocabWordRequest;
import com.back_end.english_app.dto.respones.vocab.AdminVocabWordResponse;
import com.back_end.english_app.entity.VocabTopicEntity;
import com.back_end.english_app.entity.VocabWordEntity;
import org.springframework.stereotype.Component;

@Component
public class VocabWordMapper {
    public AdminVocabWordResponse toResponse(VocabWordEntity entity){
        if(entity == null) return null;
        VocabTopicEntity topic = entity.getTopic();
        return AdminVocabWordResponse.builder()
                .id(entity.getId())
                .englishWord(entity.getEnglishWord())
                .vietnameseMeaning(entity.getVietnameseMeaning())
                .pronunciation(entity.getPronunciation())
                .audioUrl(entity.getAudioUrl())
                .imageUrl(entity.getImageUrl())
                .exampleSentence(entity.getExampleSentence())
                .exampleTranslation(entity.getExampleTranslation())
                .wordType(entity.getWordType())
                .xpReward(entity.getXpReward())
                .topicId(topic != null ? topic.getId() : null)
                .topicName(topic != null ? topic.getName() : null)
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .build();
    }

    public VocabWordEntity toEntity(AdminVocabWordRequest request) {
        VocabWordEntity entity = new VocabWordEntity();
        entity.setEnglishWord(request.getEnglishWord());
        entity.setVietnameseMeaning(request.getVietnameseMeaning());
        entity.setPronunciation(request.getPronunciation());
        entity.setAudioUrl(request.getAudioUrl());
        entity.setImageUrl(request.getImageUrl());
        entity.setExampleSentence(request.getExampleSentence());
        entity.setExampleTranslation(request.getExampleSentenceMeaning());
        entity.setWordType(request.getWordType());
        entity.setXpReward(request.getXpReward());
        entity.setIsActive(true);
        return entity;
    }
}

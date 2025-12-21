package com.back_end.english_app.dto.respones.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminVocabWordResponse {
    private Long id;
    private Long topicId;
    private String englishWord;
    private String vietnameseMeaning;
    private String pronunciation;
    private String audioUrl;
    private String imageUrl;
    private String exampleSentence;
    private String exampleTranslation;
    private String wordType;
    private Integer xpReward;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

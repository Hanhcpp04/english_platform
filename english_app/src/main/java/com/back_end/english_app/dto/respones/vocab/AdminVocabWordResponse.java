package com.back_end.english_app.dto.respones.vocab;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminVocabWordResponse {
    Long id;
    String englishWord;
    String vietnameseMeaning;
    String pronunciation;
    String audioUrl;
    String imageUrl;
    String exampleSentence;
    String exampleTranslation;
    String wordType;
    Integer xpReward;
    Boolean isActive;
    Long topicId;
    String topicName;
    LocalDateTime createdAt;
}

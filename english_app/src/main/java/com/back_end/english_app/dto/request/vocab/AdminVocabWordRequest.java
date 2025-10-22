package com.back_end.english_app.dto.request.vocab;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminVocabWordRequest {
    String englishWord;
    String vietnameseMeaning;
    String pronunciation;
    String audioUrl;
    String imageUrl;
    String exampleSentence;
    String exampleSentenceMeaning;
    Integer xpReward = 5;
    String wordType;
    Long topicId;
}

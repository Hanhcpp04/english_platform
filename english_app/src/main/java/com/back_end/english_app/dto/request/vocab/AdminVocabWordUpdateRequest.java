package com.back_end.english_app.dto.request.vocab;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminVocabWordUpdateRequest {
    String englishWord;
    String vietnameseMeaning;
    String pronunciation;
    String exampleSentence;
    String exampleSentenceMeaning;
    Integer xpReward;
    String wordType;
    Boolean isActive;
}

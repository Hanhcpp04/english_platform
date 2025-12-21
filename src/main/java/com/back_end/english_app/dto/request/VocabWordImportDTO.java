package com.back_end.english_app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabWordImportDTO {
    private String englishWord;
    private String vietnameseMeaning;
    private String pronunciation;
    private String exampleSentence;
    private String exampleTranslation;
    private String wordType;
    private Integer xpReward;
}

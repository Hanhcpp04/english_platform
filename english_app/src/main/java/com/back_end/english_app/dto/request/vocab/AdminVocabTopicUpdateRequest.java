package com.back_end.english_app.dto.request.vocab;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminVocabTopicUpdateRequest {

    String englishName;
    String name;
    String description;
    String iconUrl;
    Integer xpReward;
    Boolean isActive;
}

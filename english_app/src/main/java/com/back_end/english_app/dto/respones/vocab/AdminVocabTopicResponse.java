package com.back_end.english_app.dto.respones.vocab;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminVocabTopicResponse {
    Long id;
    String englishName;
    String name;
    String description;
    String iconUrl;
    Integer xpReward;
    Integer totalWords;
    LocalDateTime createdAt;
}
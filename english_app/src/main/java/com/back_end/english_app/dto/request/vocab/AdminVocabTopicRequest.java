package com.back_end.english_app.dto.request.vocab;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminVocabTopicRequest {

    @NotBlank(message = "Trường này là bắt buộc")
    String englishName;

    @NotBlank(message = "Trường này là bắt buộc")
    String name;

    String description;
    String iconUrl;

    Integer xpReward = 100;
}

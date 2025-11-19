package com.back_end.english_app.dto.respones.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LevelResponse {
    Integer levelNumber;
    String levelName;
    Integer minXp;
    Integer maxXp;
    String description;
    String iconUrl;
}


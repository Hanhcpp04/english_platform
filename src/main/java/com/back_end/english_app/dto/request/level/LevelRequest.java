package com.back_end.english_app.dto.request.level;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class LevelRequest {
    String levelName;
    Integer minXp;
    Integer maxXp;
    String description;
}

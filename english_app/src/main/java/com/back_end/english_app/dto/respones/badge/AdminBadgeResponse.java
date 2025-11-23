package com.back_end.english_app.dto.respones.badge;

import com.back_end.english_app.entity.ConditionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class AdminBadgeResponse {
    Long id;
    String name;
    String description;
    String iconUrl;
    String conditionType;
    Integer conditionValue;
    Integer xpReward;
    Boolean isActive;
    LocalDateTime createdAt;
}

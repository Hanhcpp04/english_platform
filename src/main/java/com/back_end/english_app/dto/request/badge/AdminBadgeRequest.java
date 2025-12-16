package com.back_end.english_app.dto.request.badge;

import com.back_end.english_app.entity.ConditionType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class AdminBadgeRequest {
    String name;
    String description;
    ConditionType conditionType;
    Integer conditionValue;
    Integer xpReward;
}

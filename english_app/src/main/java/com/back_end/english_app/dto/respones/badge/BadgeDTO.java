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
public class BadgeDTO {
     Long id;
     String name;
     String description;
     String iconUrl;
     ConditionType conditionType;
     Integer conditionValue;
     Integer xpReward;
     LocalDateTime earnedAt; // Null nếu chưa đạt được
     Boolean isEarned;
}

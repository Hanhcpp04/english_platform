package com.back_end.english_app.dto.respones.badge;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class UserBadgesSummaryDTO {
     Integer totalBadges;
     Integer earnedBadges;
     Integer unlockedBadges;
     List<BadgeDTO> recentBadges;
     List<BadgeDTO> allBadges;
}

package com.back_end.english_app.dto.respones.badge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO cho tiến độ badge của user
 * Dùng để hiển thị progress bar, current/target values
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeProgressDTO {

    private Long badgeId;

    private String badgeName;

    private String badgeDescription;

    private String iconUrl;

    private String conditionType;

    private Integer xpReward;

    /**
     * Giá trị hiện tại của user (số từ đã học, số bài đã làm, etc.)
     */
    private Integer currentValue;

    /**
     * Giá trị cần đạt để nhận badge
     */
    private Integer targetValue;

    /**
     * Phần trăm hoàn thành (0-100)
     */
    private Double progressPercentage;

    /**
     * Đã đạt được badge chưa
     */
    private Boolean isEarned;

    /**
     * Thời gian đạt được badge (null nếu chưa đạt)
     */
    private LocalDateTime earnedAt;

    /**
     * Text hiển thị progress (ví dụ: "50/100 từ vựng")
     */
    public String getProgressText() {
        if (isEarned != null && isEarned) {
            return "Đã đạt được";
        }
        return currentValue + "/" + targetValue;
    }

    /**
     * Check xem có gần đạt được badge không (>= 80%)
     */
    public boolean isNearlyComplete() {
        return progressPercentage != null && progressPercentage >= 80.0 && !Boolean.TRUE.equals(isEarned);
    }
}


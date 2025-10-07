package com.back_end.english_app.dto.respones;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileDTO {
    private String avatar;
    private String fullname;
    private String username;
    private LocalDateTime createdAt;
    private Integer totalXp;
    private List<BadgeDTO> badges;
    private Integer currentStreak;
    private Integer totalStudyDays;
}

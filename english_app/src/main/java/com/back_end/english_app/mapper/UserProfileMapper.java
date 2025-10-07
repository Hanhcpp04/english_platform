package com.back_end.english_app.mapper;

import com.back_end.english_app.dto.respones.BadgeDTO;
import com.back_end.english_app.dto.respones.UserProfileDTO;
import com.back_end.english_app.entity.BadgeEntity;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.UserStreakEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class UserProfileMapper {
    public UserProfileDTO toDTO(UserEntity user, List<BadgeDTO> badges, UserStreakEntity streak){
        UserProfileDTO dto = new UserProfileDTO();
        dto.setAvatar(user.getAvatar());
        dto.setFullname(user.getFullname());
        dto.setUsername(user.getUsername());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setTotalXp(user.getTotalXp());
        dto.setBadges(badges);
        if(streak != null){
            dto.setCurrentStreak(streak.getCurrentStreak());
            dto.setTotalStudyDays(streak.getTotalStudyDays());
        }
        return dto;
    }
    public BadgeDTO toBadgeDTO(BadgeEntity badge){
        BadgeDTO dto = new BadgeDTO();
        dto.setId(badge.getId());
        dto.setIconUrl(badge.getIconUrl());
        return dto;
    }
}

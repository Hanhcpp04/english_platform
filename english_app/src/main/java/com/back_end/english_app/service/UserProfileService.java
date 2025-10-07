package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.BadgeDTO;
import com.back_end.english_app.dto.respones.UserProfileDTO;
import com.back_end.english_app.entity.UserBadgeEntity;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.UserStreakEntity;
import com.back_end.english_app.mapper.UserProfileMapper;
import com.back_end.english_app.repository.BadgeRepository;
import com.back_end.english_app.repository.UserBadgeRepository;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.repository.UserStreakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final UserStreakRepository userStreakRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileDTO getUserProfile(Long id){
        // lay user
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));
        // lay danh sach badges
        List<UserBadgeEntity> userBadgeEntity = userBadgeRepository.findByUserId(user.getId());
        List<BadgeDTO> badges = userBadgeEntity.stream()
                .map(ub -> badgeRepository.findById(ub.getId())
                        .map(userProfileMapper::toBadgeDTO)
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // lay streak
        UserStreakEntity streak = userStreakRepository.findByUserId(user.getId())
                .orElse(null);
        // map sang DTO
        return userProfileMapper.toDTO(user, badges, streak);
    }
    // cập nhật avatar (upload file)

}

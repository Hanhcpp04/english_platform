package com.back_end.english_app.service;

import com.back_end.english_app.dto.respones.badge.BadgeDTO;
import com.back_end.english_app.dto.respones.badge.UserBadgesSummaryDTO;
import com.back_end.english_app.entity.BadgeEntity;
import com.back_end.english_app.entity.UserBadgeEntity;
import com.back_end.english_app.repository.BadgeRepository;
import com.back_end.english_app.repository.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    @Transactional(readOnly = true)
    public UserBadgesSummaryDTO getUserBadgesSummary(Long userId, Integer recentLimit) {
        if (recentLimit == null || recentLimit <= 0) {
            recentLimit = 5; // Mặc định lấy 5 huy hiệu gần nhất
        }

        // đếm tổng số huy hiệu
        Long totalBadges = badgeRepository.countByIsActiveTrue();

        // dếm số huy hiệu đã đạt được
        Long earnedBadges = userBadgeRepository.countByUserId(userId);

        // lấy huy hiệu gần đây
        List<UserBadgeEntity> recentUserBadges = userBadgeRepository
                .findRecentBadgesByUserId(userId, PageRequest.of(0, recentLimit));

        List<BadgeDTO> recentBadges = recentUserBadges.stream()
                .map(ub -> convertToDTO(ub.getBadge(), ub.getEarnedAt()))
                .collect(Collectors.toList());

        return UserBadgesSummaryDTO.builder()
                .totalBadges(totalBadges.intValue())
                .earnedBadges(earnedBadges.intValue())
                .unlockedBadges(totalBadges.intValue() - earnedBadges.intValue())
                .recentBadges(recentBadges)
                .build();
    }

    @Transactional(readOnly = true)
    public UserBadgesSummaryDTO getAllUserBadges(Long userId) {
        // đếm tổng số huy hiệu
        Long totalBadges = badgeRepository.countByIsActiveTrue();

        // đếm số huy hiệu đã đạt được
        Long earnedBadges = userBadgeRepository.countByUserId(userId);

        // lấy tất cả huy hiệu đã đạt
        List<UserBadgeEntity> userBadges = userBadgeRepository.findAllByUserId(userId);
        Map<Long, UserBadgeEntity> userBadgeMap = new HashMap<>();
        for (UserBadgeEntity ub : userBadges) {
            userBadgeMap.put(ub.getBadge().getId(), ub);
        }

        // lấy tất cả badges và đánh dấu đã đạt hay chưa
        List<BadgeEntity> allBadges = badgeRepository.findAllByIsActiveTrueOrderByCreatedAtDesc();
        List<BadgeDTO> badgeDTOs = allBadges.stream()
                .map(badge -> {
                    UserBadgeEntity userBadge = userBadgeMap.get(badge.getId());
                    return convertToDTO(badge, userBadge != null ? userBadge.getEarnedAt() : null);
                })
                .collect(Collectors.toList());

        // lấy 5 huy hiệu gần nhất
        List<BadgeDTO> recentBadges = badgeDTOs.stream()
                .filter(BadgeDTO::getIsEarned)
                .limit(5)
                .collect(Collectors.toList());

        return UserBadgesSummaryDTO.builder()
                .totalBadges(totalBadges.intValue())
                .earnedBadges(earnedBadges.intValue())
                .unlockedBadges(totalBadges.intValue() - earnedBadges.intValue())
                .recentBadges(recentBadges)
                .allBadges(badgeDTOs)
                .build();
    }

    @Transactional(readOnly = true)
    public List<BadgeDTO> getRecentBadges(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 5;
        }

        List<UserBadgeEntity> recentUserBadges = userBadgeRepository
                .findRecentBadgesByUserId(userId, PageRequest.of(0, limit));

        return recentUserBadges.stream()
                .map(ub -> convertToDTO(ub.getBadge(), ub.getEarnedAt()))
                .collect(Collectors.toList());
    }

    private BadgeDTO convertToDTO(BadgeEntity badge, java.time.LocalDateTime earnedAt) {
        return BadgeDTO.builder()
                .id(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .iconUrl("")
                .conditionType(badge.getConditionType())
                .conditionValue(badge.getConditionValue())
                .xpReward(badge.getXpReward())
                .earnedAt(earnedAt)
                .isEarned(earnedAt != null)
                .build();
    }

}

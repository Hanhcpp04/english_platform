package com.back_end.english_app.service.user;

import com.back_end.english_app.dto.respones.badge.BadgeProgressDTO;
import com.back_end.english_app.entity.*;
import com.back_end.english_app.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeCheckService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserBadgeProgressRepository userBadgeProgressRepository;
    private final UserService userService;
    private final FileUploadService fileUploadService;

    // Repositories để tính toán giá trị
    private final VocabUserProgressRepository vocabProgressRepository;
    private final UserGrammarProgressRepository grammarProgressRepository;
    private final WritingPromptRepository writingPromptRepository;
    private final ForumPostRepository forumPostRepository;
    private final UserStreakRepository userStreakRepository;

    /**
     * Cache badges theo condition type để tránh query DB liên tục
     */
    @Cacheable(value = "badgesByType", key = "#type")
    private List<BadgeEntity> getBadgesByType(ConditionType type) {
        log.debug("Loading badges from DB for type: {}", type);
        return badgeRepository.findByConditionTypeAndIsActiveTrue(type);
    }

    @Transactional
    public void checkAndUpdateBadges(Long userId, String conditionType) {
        log.info("Checking badges for user {} with condition type: {}", userId, conditionType);

        // 1. Lấy tất cả badges active theo loại (sử dụng cache)
        ConditionType type = ConditionType.valueOf(conditionType.toUpperCase());
        List<BadgeEntity> badges = getBadgesByType(type);

        if (badges.isEmpty()) {
            log.debug("No active badges found for condition type: {}", conditionType);
            return;
        }

        // 2. Tính giá trị hiện tại của user
        int currentValue = calculateUserValue(userId, conditionType);
        log.debug("User {} current value for {}: {}", userId, conditionType, currentValue);

        // 3. OPTIMIZATION: Batch load user badges và progress một lần
        List<Long> badgeIds = badges.stream().map(BadgeEntity::getId).toList();
        
        // Load tất cả user badges của user cho các badge IDs này
        List<UserBadgeEntity> userBadges = userBadgeRepository.findByUserIdAndBadgeIdIn(userId, badgeIds);
        List<Long> earnedBadgeIds = userBadges.stream()
                .map(ub -> ub.getBadge().getId())
                .toList();
        
        // Load tất cả badge progress của user cho các badge IDs này
        List<UserBadgeProgressEntity> progressList = userBadgeProgressRepository.findByUserIdAndBadgeIdIn(userId, badgeIds);
        java.util.Map<Long, UserBadgeProgressEntity> progressMap = progressList.stream()
                .collect(java.util.stream.Collectors.toMap(
                    UserBadgeProgressEntity::getBadgeId,
                    p -> p
                ));

        // 4. Kiểm tra từng badge với data đã load
        for (BadgeEntity badge : badges) {
            try {
                processBadgeOptimized(userId, badge, currentValue, earnedBadgeIds, progressMap);
            } catch (Exception e) {
                log.error("Error processing badge {} for user {}: {}",
                    badge.getId(), userId, e.getMessage(), e);
            }
        }
    }

    /**
     * Xử lý một badge cụ thể - Optimized version
     */
    private void processBadgeOptimized(Long userId, BadgeEntity badge, int currentValue,
                                       List<Long> earnedBadgeIds,
                                       java.util.Map<Long, UserBadgeProgressEntity> progressMap) {
        // Kiểm tra xem user đã có badge này chưa (từ data đã load)
        boolean alreadyEarned = earnedBadgeIds.contains(badge.getId());

        if (alreadyEarned) {
            log.debug("User {} already earned badge {}", userId, badge.getId());
            return;
        }

        // Lấy progress từ map (nếu có)
        UserBadgeProgressEntity progress = progressMap.get(badge.getId());
        
        // OPTIMIZATION: Nếu progress đã 100% thì không cần update lại
        if (progress != null && progress.getProgressPercentage().compareTo(BigDecimal.valueOf(100)) >= 0) {
            log.debug("Badge progress already at 100%, skipping update");
            return;
        }

        // Cập nhật progress
        updateBadgeProgressOptimized(userId, badge, currentValue, progress);

        // Nếu đạt điều kiện, trao badge
        if (currentValue >= badge.getConditionValue()) {
            awardBadge(userId, badge);
        }
    }

    /**
     * Phương thức cũ giữ lại để backward compatibility
     */
    private void processBadge(Long userId, BadgeEntity badge, int currentValue) {
        // Kiểm tra xem user đã có badge này chưa
        boolean alreadyEarned = userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId());

        if (alreadyEarned) {
            log.debug("User {} already earned badge {}", userId, badge.getId());
            return;
        }

        // Cập nhật progress
        updateBadgeProgress(userId, badge, currentValue);

        // Nếu đạt điều kiện, trao badge
        if (currentValue >= badge.getConditionValue()) {
            awardBadge(userId, badge);
        }
    }


    private int calculateUserValue(Long userId, String conditionType) {
        try {
            switch (conditionType.toLowerCase()) {
                case "vocabulary":
                    // Đếm số từ vựng đã hoàn thành
                    return vocabProgressRepository.countByUserIdAndIsCompletedTrue(userId);

                case "grammar":
                    // Đếm số bài ngữ pháp đã hoàn thành (đếm distinct lesson_id)
                    return grammarProgressRepository.countDistinctLessonsByUserIdAndIsCompletedTrue(userId);

                case "writing":
                    // Đếm số bài viết đã hoàn thành
                    long completedCount = writingPromptRepository.countByUserIdAndIsCompletedTrue(userId);
                    // Safe cast to int with cap to Integer.MAX_VALUE
                    return completedCount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) completedCount;

                case "forum":
                    // Đếm số bài viết diễn đàn
                    return forumPostRepository.countByUserIdAndIsActiveTrue(userId);

                case "streak":
                    // Lấy streak hiện tại
                    return userStreakRepository.findByUserId(userId)
                            .map(UserStreakEntity::getCurrentStreak)
                            .orElse(0);


                default:
                    log.warn("Unknown condition type: {}", conditionType);
                    return 0;
            }
        } catch (Exception e) {
            log.error("Error calculating user value for type {}: {}", conditionType, e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Cập nhật tiến độ badge của user - Optimized version
     */
    private void updateBadgeProgressOptimized(Long userId, BadgeEntity badge, int currentValue, 
                                              UserBadgeProgressEntity existingProgress) {
        UserBadgeProgressEntity progress = existingProgress;
        
        if (progress == null) {
            progress = new UserBadgeProgressEntity();
            progress.setUserId(userId);
            progress.setBadgeId(badge.getId());
        }

        progress.setCurrentValue(currentValue);
        progress.setTargetValue(badge.getConditionValue());

        // Tính phần trăm
        BigDecimal percentage = BigDecimal.valueOf(currentValue)
                .divide(BigDecimal.valueOf(badge.getConditionValue()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        // Cap at 100%
        if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            percentage = BigDecimal.valueOf(100);
        }
        progress.setProgressPercentage(percentage);

        userBadgeProgressRepository.save(progress);

        log.debug("Updated badge progress for user {} - badge {}: {}/{} ({}%)",
            userId, badge.getId(), currentValue, badge.getConditionValue(),
            percentage);
    }

    /**
     * Cập nhật tiến độ badge của user
     */
    private void updateBadgeProgress(Long userId, BadgeEntity badge, int currentValue) {
        UserBadgeProgressEntity progress = userBadgeProgressRepository
            .findByUserIdAndBadgeId(userId, badge.getId())
            .orElse(new UserBadgeProgressEntity());

        progress.setUserId(userId);
        progress.setBadgeId(badge.getId());
        progress.setCurrentValue(currentValue);
        progress.setTargetValue(badge.getConditionValue());

        // Tính phần trăm
        BigDecimal percentage = BigDecimal.valueOf(currentValue)
                .divide(BigDecimal.valueOf(badge.getConditionValue()), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        // Cap at 100%
        if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            percentage = BigDecimal.valueOf(100);
        }
        progress.setProgressPercentage(percentage);

        userBadgeProgressRepository.save(progress);

        log.debug("Updated badge progress for user {} - badge {}: {}/{} ({}%)",
            userId, badge.getId(), currentValue, badge.getConditionValue(),
            percentage);
    }

    /**
     * Trao badge cho user
     */
    @Transactional
    private void awardBadge(Long userId, BadgeEntity badge) {
        try {
            // 1. Tạo record trong user_badges
            UserEntity user = userService.getUserById(userId);
            UserBadgeEntity userBadge = new UserBadgeEntity();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadge.setEarnedAt(LocalDateTime.now());
            userBadgeRepository.save(userBadge);

            // 2. Cộng XP cho user
            userService.addXP(userId, badge.getXpReward());

            // 3. Update progress to 100%
            updateBadgeProgress(userId, badge, badge.getConditionValue());

            // 4. Log event
            log.info("🎉 User {} earned badge: '{}' (+{} XP)",
                userId, badge.getName(), badge.getXpReward());

            // 5. TODO: Gửi notification (implement sau)
            // notificationService.sendBadgeNotification(userId, badge);

        } catch (Exception e) {
            log.error("Error awarding badge {} to user {}: {}",
                badge.getId(), userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Lấy tiến độ của tất cả badges cho user
     * Dùng để hiển thị trong UI
     */
    @Transactional(readOnly = true)
    public List<BadgeProgressDTO> getAllBadgeProgress(Long userId) {
        List<BadgeEntity> allBadges = badgeRepository.findByIsActiveTrueOrderByConditionTypeAscConditionValueAsc();
        List<BadgeProgressDTO> progressList = new ArrayList<>();

        for (BadgeEntity badge : allBadges) {
            BadgeProgressDTO dto = new BadgeProgressDTO();
            dto.setBadgeId(badge.getId());
            dto.setBadgeName(badge.getName());
            dto.setBadgeDescription(badge.getDescription());
            dto.setIconUrl(fileUploadService.buildFullUrl("badge/" + badge.getIconUrl()));
            dto.setConditionType(badge.getConditionType().name());
            dto.setXpReward(badge.getXpReward());

            // Kiểm tra đã earned chưa
            boolean earned = userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId());
            dto.setIsEarned(earned);

            if (earned) {
                // Đã đạt được -> 100%
                dto.setCurrentValue(badge.getConditionValue());
                dto.setTargetValue(badge.getConditionValue());
                dto.setProgressPercentage(100.0);

                // Lấy thời gian earned
                userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId())
                    .ifPresent(ub -> dto.setEarnedAt(ub.getEarnedAt()));
            } else {
                // Chưa đạt được -> lấy progress
                UserBadgeProgressEntity progress = userBadgeProgressRepository
                    .findByUserIdAndBadgeId(userId, badge.getId())
                    .orElse(null);

                if (progress != null) {
                    dto.setCurrentValue(progress.getCurrentValue());
                    dto.setTargetValue(progress.getTargetValue());
                    dto.setProgressPercentage(progress.getProgressPercentage().doubleValue());
                } else {
                    // Chưa có progress -> 0%
                    dto.setCurrentValue(0);
                    dto.setTargetValue(badge.getConditionValue());
                    dto.setProgressPercentage(0.0);
                }
            }

            progressList.add(dto);
        }

        return progressList;
    }

    /**
     * Lấy tiến độ badges theo loại
     */
    @Transactional(readOnly = true)
    public List<BadgeProgressDTO> getBadgeProgressByType(Long userId, String conditionType) {
        ConditionType type = ConditionType.valueOf(conditionType.toUpperCase());
        List<BadgeEntity> badges = badgeRepository.findByConditionTypeAndIsActiveTrueOrderByConditionValueAsc(type);
        List<BadgeProgressDTO> progressList = new ArrayList<>();

        int currentValue = calculateUserValue(userId, conditionType);

        for (BadgeEntity badge : badges) {
            BadgeProgressDTO dto = new BadgeProgressDTO();
            dto.setBadgeId(badge.getId());
            dto.setBadgeName(badge.getName());
            dto.setBadgeDescription(badge.getDescription());
            dto.setIconUrl(fileUploadService.buildFullUrl("badge/" + badge.getIconUrl()));
            dto.setConditionType(badge.getConditionType().name());
            dto.setXpReward(badge.getXpReward());

            boolean earned = userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId());
            dto.setIsEarned(earned);

            if (earned) {
                dto.setCurrentValue(badge.getConditionValue());
                dto.setTargetValue(badge.getConditionValue());
                dto.setProgressPercentage(100.0);

                userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId())
                    .ifPresent(ub -> dto.setEarnedAt(ub.getEarnedAt()));
            } else {
                dto.setCurrentValue(currentValue);
                dto.setTargetValue(badge.getConditionValue());
                double percentage = Math.min((double) currentValue / badge.getConditionValue() * 100.0, 100.0);
                dto.setProgressPercentage(percentage);
            }

            progressList.add(dto);
        }

        return progressList;
    }

    /**
     * Kiểm tra tất cả loại badges cho user
     * Dùng khi cần sync lại hoặc user vừa login
     */
    @Transactional
    public void checkAllBadges(Long userId) {
        log.info("Checking all badges for user {}", userId);

        checkAndUpdateBadges(userId, "vocabulary");
        checkAndUpdateBadges(userId, "grammar");
        checkAndUpdateBadges(userId, "writing");
        checkAndUpdateBadges(userId, "forum");
        checkAndUpdateBadges(userId, "streak");
        checkAndUpdateBadges(userId, "accuracy");
    }
}


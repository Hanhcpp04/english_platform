package com.back_end.english_app.controller.badge;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.badge.BadgeDTO;
import com.back_end.english_app.dto.respones.badge.BadgeProgressDTO;
import com.back_end.english_app.dto.respones.badge.UserBadgesSummaryDTO;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.service.user.BadgeCheckService;
import com.back_end.english_app.service.user.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/badge")
@RequiredArgsConstructor
public class BadgeController {
    private final BadgeService badgeService;
    private final BadgeCheckService badgeCheckService;
    private final UserRepository userRepository;

    // Helper method to get userId from Authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    // lấy tổng số huy hiệu và huy hiệu gần đây
    @GetMapping("/summary/{userId}")
    public APIResponse<UserBadgesSummaryDTO> getUserBadgesSummary(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        UserBadgesSummaryDTO summary = badgeService.getUserBadgesSummary(userId, limit);
        return APIResponse.success(summary);
    }

    //    tất cả huy hiệu (đã đạt và chưa đạt)
    @GetMapping("/all/{userId}")
    public APIResponse<UserBadgesSummaryDTO> getAllUserBadges(@PathVariable Long userId) {
        UserBadgesSummaryDTO badges = badgeService.getAllUserBadges(userId);
        return APIResponse.success(badges);
    }

    //    Lấy danh sách huy hiệu gần đây
    @GetMapping("/recent/{userId}")
    public APIResponse<List<BadgeDTO>> getRecentBadges(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        List<BadgeDTO> recentBadges = badgeService.getRecentBadges(userId, limit);
        return APIResponse.success(recentBadges);
    }

//    Lấy progress của tất cả badges cho user đang login
    @GetMapping("/progress")
    public APIResponse<List<BadgeProgressDTO>> getBadgeProgress(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<BadgeProgressDTO> progress = badgeCheckService.getAllBadgeProgress(userId);
        return APIResponse.success(progress);
    }

//    Lấy progress theo loại (vocabulary, grammar, etc.)
    @GetMapping("/progress/{type}")
    public APIResponse<List<BadgeProgressDTO>> getBadgeProgressByType(
            @PathVariable String type,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<BadgeProgressDTO> progress = badgeCheckService.getBadgeProgressByType(userId, type);
        return APIResponse.success(progress);
    }

//    Lấy summary và badges đã earned
    @GetMapping("/earned")
    public APIResponse<UserBadgesSummaryDTO> getEarnedBadges(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserBadgesSummaryDTO badges = badgeService.getAllUserBadges(userId);
        return APIResponse.success(badges);
    }

    /**
     * POST /api/badge/check
     * Force check all badges (để test hoặc sync)
     */
    @PostMapping("/check")
    public APIResponse<Map<String, Object>> forceCheckBadges(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        badgeCheckService.checkAllBadges(userId);

        Map<String, Object> data = Map.of(
                "success", true,
                "message", "All badges checked successfully"
        );

        return APIResponse.success(data);
    }


}

package com.back_end.english_app.controller.badge;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.badge.BadgeDTO;
import com.back_end.english_app.dto.respones.badge.UserBadgesSummaryDTO;
import com.back_end.english_app.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badge")
@RequiredArgsConstructor
public class Badge {
    private final BadgeService badgeService;

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
    public ResponseEntity<List<BadgeDTO>> getRecentBadges(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        List<BadgeDTO> recentBadges = badgeService.getRecentBadges(userId, limit);
        return ResponseEntity.ok(recentBadges);
    }
}

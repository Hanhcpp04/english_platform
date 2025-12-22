package com.back_end.english_app.controller.dashboard;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.dashboard.DashboardSummaryDTO;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.service.user.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    // Helper method to get userId from Authentication
    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    /**
     * GET /api/dashboard/summary
     * Lấy tổng quan dashboard cho user đang login
     */
    @GetMapping("/summary")
    public APIResponse<DashboardSummaryDTO> getDashboardSummary(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        log.info("Getting dashboard summary for userId: {}", userId);
        
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userId);
        return APIResponse.success(summary);
    }

    /**
     * GET /api/dashboard/summary/{userId}
     * Lấy tổng quan dashboard cho một user cụ thể (có thể dùng cho admin)
     */
    @GetMapping("/summary/{userId}")
    public APIResponse<DashboardSummaryDTO> getDashboardSummaryByUserId(@PathVariable Long userId) {
        log.info("Getting dashboard summary for userId: {}", userId);
        
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary(userId);
        return APIResponse.success(summary);
    }
}

package com.back_end.english_app.controller.admin;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.admin.AdminDashboardDTO;
import com.back_end.english_app.service.admin.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    /**
     * GET /api/admin/dashboard
     * Lấy toàn bộ dữ liệu dashboard cho admin
     */
    @GetMapping
    public APIResponse<AdminDashboardDTO> getAdminDashboard() {
        log.info("Getting admin dashboard data");
        AdminDashboardDTO dashboard = adminDashboardService.getAdminDashboard();
        return APIResponse.success(dashboard);
    }
}

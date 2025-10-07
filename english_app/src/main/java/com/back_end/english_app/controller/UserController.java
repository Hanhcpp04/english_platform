package com.back_end.english_app.controller;

import com.back_end.english_app.dto.ApiResponse;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "API quản lý người dùng")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/profile")
    @Operation(summary = "Lấy thông tin profile người dùng hiện tại")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("fullname", user.getFullname());
        profile.put("role", user.getRole().name());
        profile.put("totalXp", user.getTotalXp());
        profile.put("isEmailVerified", user.getIsEmailVerified());
        profile.put("isActive", user.getIsActive());
        profile.put("createdAt", user.getCreatedAt());
        
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin profile thành công", profile));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard thống kê người dùng")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("welcomeMessage", "Chào mừng " + user.getFullname() + "!");
        dashboard.put("totalXp", user.getTotalXp());
        dashboard.put("level", calculateLevel(user.getTotalXp()));
        dashboard.put("emailVerified", user.getIsEmailVerified());
        dashboard.put("accountStatus", user.getIsActive() ? "Active" : "Inactive");
        
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded successfully", dashboard));
    }

    private int calculateLevel(Integer totalXp) {
        if (totalXp == null || totalXp < 100) return 1;
        if (totalXp < 500) return 2;
        if (totalXp < 1000) return 3;
        if (totalXp < 2000) return 4;
        return 5;
    }
}

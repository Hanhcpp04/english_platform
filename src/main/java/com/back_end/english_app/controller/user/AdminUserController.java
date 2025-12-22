package com.back_end.english_app.controller.user;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.user.AdminUserResponse;
import com.back_end.english_app.entity.Role;
import com.back_end.english_app.service.admin.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin-users")
@RequiredArgsConstructor
public class AdminUserController {
    public final AdminUserService adminUserService;

    @GetMapping("/getAll")
    public APIResponse<Page<AdminUserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return adminUserService.getAllUsers(page, size);
    }

    //cấp quyền
    @PutMapping("/update-role/{id}/{role}")
    public APIResponse<String> updateRole(
            @PathVariable Long id,
            @PathVariable Role role) {
        return adminUserService.updatePermissionById(id, role);
    }

    //vô hiệu hóa hoặc khôi phục tài khoản, status gồm delete và restore
    @PutMapping("/delete-restore/{id}/{status}")
    public APIResponse<String> deleteUserById(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        return adminUserService.deleteOrRestoreUser(id, status);
    }

    //Xem chi tiết profile user
    @GetMapping("/profile/{userId}")
    public APIResponse<AdminUserResponse> getUserProfile(@PathVariable Long userId) {
        return adminUserService.getUserProfile(userId);
    }

}

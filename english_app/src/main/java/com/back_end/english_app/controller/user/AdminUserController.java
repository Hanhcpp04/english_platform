package com.back_end.english_app.controller.user;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.user.AdminUserResponse;
import com.back_end.english_app.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AdminUserController {
    public final AdminUserService adminUserService;

    @GetMapping()
    public APIResponse<List<AdminUserResponse>> getAllUsers(){
        return adminUserService.getAllUsers();
    }

    @PutMapping("/{id}")
    public APIResponse<String> deleteUserById(@PathVariable Long id){
        return adminUserService.deleteUser(id);
    }
}

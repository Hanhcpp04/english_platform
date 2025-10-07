package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.UserProfileDTO;
import com.back_end.english_app.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserProfileController {
    private final UserProfileService userProfileService;
    //Xem profile
    @GetMapping("/{id}")
    public APIResponse<UserProfileDTO> getProfile(@PathVariable Long id){
        UserProfileDTO user = userProfileService.getUserProfile(id);
        return APIResponse.<UserProfileDTO>builder()
                .result(user)
                .build();
    }
    //Sua avt (de sau i)

}
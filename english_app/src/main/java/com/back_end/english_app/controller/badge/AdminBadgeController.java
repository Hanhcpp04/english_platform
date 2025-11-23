package com.back_end.english_app.controller.badge;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.badge.AdminBadgeRequest;
import com.back_end.english_app.dto.request.badge.AdminBadgeUpdateRequest;
import com.back_end.english_app.dto.respones.badge.AdminBadgeResponse;
import com.back_end.english_app.service.AdminBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
public class AdminBadgeController {
    public final AdminBadgeService adminBadgeService;

    @GetMapping()
    public APIResponse<List<AdminBadgeResponse>> getAllBadges(){
        return adminBadgeService.getAllBadges();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> addNewBadge(
            @RequestPart("data") AdminBadgeRequest request,
            @RequestPart("icon") MultipartFile iconFile
    ){
        return adminBadgeService.addNewBadge(request, iconFile);
    }

    //sửa thông tin huy hiệu
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> updateBadge(
            @PathVariable Long id,
            @RequestPart("data") AdminBadgeUpdateRequest request,
            @RequestPart(value = "icon", required = false) MultipartFile iconFile
    ) {
        return adminBadgeService.updateBadge(id, request, iconFile);
    }
}

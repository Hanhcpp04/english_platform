package com.back_end.english_app.controller.badge;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.badge.AdminBadgeRequest;
import com.back_end.english_app.dto.respones.badge.AdminBadgeResponse;
import com.back_end.english_app.service.AdminBadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin-badges")
@RequiredArgsConstructor
public class AdminBadgeController {
    public final AdminBadgeService adminBadgeService;

    //get all
    @GetMapping("/getAll")
    public APIResponse<Page<AdminBadgeResponse>> getAllBadges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return adminBadgeService.getAllBadges(page, size);
    }

    //tạo huy hiệu mới
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> addNewBadge(
            @RequestPart("data") AdminBadgeRequest request,
            @RequestPart("icon") MultipartFile iconFile
    ){
        return adminBadgeService.addNewBadge(request, iconFile);
    }

    //sửa thông tin huy hiệu
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public APIResponse<?> updateBadge(
            @PathVariable Long id,
            @RequestPart("data") AdminBadgeRequest request,
            @RequestPart(value = "icon", required = false) MultipartFile iconFile
    ) {
        return adminBadgeService.updateBadge(id, request, iconFile);
    }

    //xóa hoặc khôi phục huy hiệu
    @PutMapping("/delete-restore/{id}/{status}")
    public APIResponse<String> deleteOrRestoreBadgeById(
            @PathVariable Long id,
            @PathVariable String status
    ) {
        return adminBadgeService.deleteOrRestoreBadge(id, status);
    }

}

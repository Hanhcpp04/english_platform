package com.back_end.english_app.service;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.badge.AdminBadgeRequest;
import com.back_end.english_app.dto.request.badge.AdminBadgeUpdateRequest;
import com.back_end.english_app.dto.respones.badge.AdminBadgeResponse;
import com.back_end.english_app.dto.respones.user.AdminUserResponse;
import com.back_end.english_app.entity.BadgeEntity;
import com.back_end.english_app.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminBadgeService {
    private final BadgeRepository badgeRepository;

    //get all
    public APIResponse<List<AdminBadgeResponse>> getAllBadges(){
        List<BadgeEntity> badges = badgeRepository.findAll();
        return APIResponse.success(badges.stream().map(badge -> new AdminBadgeResponse(
                badge.getId(),
                badge.getName(),
                badge.getDescription(),
                badge.getIconUrl(),
                badge.getConditionType().name(),
                badge.getConditionValue(),
                badge.getXpReward(),
                badge.getIsActive(),
                badge.getCreatedAt()
        )).toList());
    }

    //thêm huy hiệu mới
    public APIResponse<?> addNewBadge(AdminBadgeRequest request, MultipartFile iconUrl) {
        // lỗi file null
        if (iconUrl == null || iconUrl.isEmpty()) {
            return APIResponse.error("File icon không được để trống");
        }

        try {
            BadgeEntity newBadge = new BadgeEntity();

            newBadge.setName(request.getName());
            newBadge.setDescription(request.getDescription());
            newBadge.setConditionType(request.getConditionType());
            newBadge.setConditionValue(request.getConditionValue());
            newBadge.setXpReward(request.getXpReward() != null ? request.getXpReward() : 0);

            // Tạo tên file
            String fileName = UUID.randomUUID().toString() + "_" + iconUrl.getOriginalFilename();

            Path uploadDir = Paths.get("uploads/badge");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(fileName);
            Files.copy(iconUrl.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            newBadge.setIconUrl(fileName);

            badgeRepository.save(newBadge);

            return APIResponse.success(
                    AdminBadgeResponse.builder()
                            .id(newBadge.getId())
                            .name(newBadge.getName())
                            .description(newBadge.getDescription())
                            .iconUrl(newBadge.getIconUrl())
                            .conditionType(newBadge.getConditionType().name())
                            .conditionValue(newBadge.getConditionValue())
                            .xpReward(newBadge.getXpReward())
                            .createdAt(newBadge.getCreatedAt())
                            .build()
            );

        } catch (IOException e) {
            return APIResponse.error("Lỗi xử lý file: " + e.getMessage());
        } catch (Exception e) {
            return APIResponse.error("Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }

    //sửa huy hiệu
    public APIResponse<?> updateBadge(Long id, AdminBadgeUpdateRequest request, MultipartFile iconFile) {
        Optional<BadgeEntity> optBadge = badgeRepository.findById(id);
        if (optBadge.isEmpty()) {
            return APIResponse.error("Huy hiệu không tồn tại");
        }

        BadgeEntity badge = optBadge.get();

        try {
            // Cập nhật các field nếu có
            if (request.getName() != null) badge.setName(request.getName());
            if (request.getDescription() != null) badge.setDescription(request.getDescription());
            if (request.getConditionType() != null) badge.setConditionType(request.getConditionType());
            if (request.getConditionValue() != null) badge.setConditionValue(request.getConditionValue());
            if (request.getXpReward() != null) badge.setXpReward(request.getXpReward());
            if (request.getIsActive() != null) badge.setIsActive(request.getIsActive());

            // Xử lý file icon mới nếu có
            if (iconFile != null && !iconFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + iconFile.getOriginalFilename();
                Path uploadDir = Paths.get("uploads/badge");
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path filePath = uploadDir.resolve(fileName);
                Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                badge.setIconUrl(fileName);
            }

            badgeRepository.save(badge);

            return APIResponse.success(
                    AdminBadgeResponse.builder()
                            .id(badge.getId())
                            .name(badge.getName())
                            .description(badge.getDescription())
                            .iconUrl(badge.getIconUrl())
                            .conditionType(badge.getConditionType().name())
                            .conditionValue(badge.getConditionValue())
                            .xpReward(badge.getXpReward())
                            .isActive(badge.getIsActive())
                            .createdAt(badge.getCreatedAt())
                            .build()
            );

        } catch (IOException e) {
            return APIResponse.error("Lỗi xử lý file: " + e.getMessage());
        } catch (Exception e) {
            return APIResponse.error("Lỗi lưu dữ liệu: " + e.getMessage());
        }
    }

}

package com.back_end.english_app.service;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.badge.AdminBadgeRequest;
import com.back_end.english_app.dto.respones.badge.AdminBadgeResponse;
import com.back_end.english_app.entity.BadgeEntity;
import com.back_end.english_app.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public APIResponse<Page<AdminBadgeResponse>> getAllBadges(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BadgeEntity> badgesPage = badgeRepository.findAll(pageable);

            Page<AdminBadgeResponse> responsePage = badgesPage.map(badge -> new AdminBadgeResponse(
                    badge.getId(),
                    badge.getName(),
                    badge.getDescription(),
                    badge.getIconUrl(),
                    badge.getConditionType().name(),
                    badge.getConditionValue(),
                    badge.getXpReward(),
                    badge.getIsActive(),
                    badge.getCreatedAt()
            ));

            return APIResponse.success(responsePage);

        } catch (Exception e) {
            return APIResponse.error("Lỗi truy xuất cơ sở dữ liệu: " + e.getMessage());
        }
    }

    //thêm huy hiệu mới
    public APIResponse<?> addNewBadge(AdminBadgeRequest request, MultipartFile iconUrl) {
        // lỗi trùng tên huy hiệu
        Optional<BadgeEntity> existBadge = badgeRepository.findByName(request.getName());
        if(existBadge.isPresent()){
            return APIResponse.error("Tên huy hiệu đã tồn tại, vui lòng nhập tên khác");
        }

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
                            .isActive(newBadge.getIsActive())
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
    public APIResponse<?> updateBadge(Long id, AdminBadgeRequest request, MultipartFile iconFile) {
        Optional<BadgeEntity> optBadge = badgeRepository.findById(id);
        if (optBadge.isEmpty()) {
            return APIResponse.error("Huy hiệu không tồn tại");
        }

        BadgeEntity badge = optBadge.get();

        // Kiểm tra trùng tên nếu user truyền name mới
        if (request.getName() != null) {
            Optional<BadgeEntity> badgeByName = badgeRepository.findByName(request.getName());

            // Nếu tìm thấy badge khác có tên trùng
            if (badgeByName.isPresent() && !badgeByName.get().getId().equals(badge.getId())) {
                return APIResponse.error("Tên huy hiệu đã tồn tại, vui lòng nhập tên khác");
            }
            badge.setName(request.getName());
        }

        if (request.getDescription() != null) badge.setDescription(request.getDescription());
        if (request.getConditionType() != null) badge.setConditionType(request.getConditionType());
        if (request.getConditionValue() != null) badge.setConditionValue(request.getConditionValue());
        if (request.getXpReward() != null) badge.setXpReward(request.getXpReward());

        // Xử lý file icon mới nếu có
        if (iconFile != null && !iconFile.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + iconFile.getOriginalFilename();
            Path uploadDir = Paths.get("uploads/badge");
            if (!Files.exists(uploadDir)) {
                try {
                    Files.createDirectories(uploadDir);
                } catch (IOException e) {
                    return APIResponse.error("Lỗi tạo thư mục: " + e.getMessage());
                }
            }

            Path filePath = uploadDir.resolve(fileName);
            try {
                Files.copy(iconFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                badge.setIconUrl(fileName);
            } catch (IOException e) {
                return APIResponse.error("Lỗi xử lý file: " + e.getMessage());
            }
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
    }
    //xóa hoặc khôi phục huy hiệu
    public APIResponse<String> deleteOrRestoreBadge(Long id, String status) {
        // Tìm badge theo id
        Optional<BadgeEntity> optBadge = badgeRepository.findById(id);
        if (optBadge.isEmpty()) {
            return APIResponse.error("Huy hiệu không tồn tại");
        }
        BadgeEntity badge = optBadge.get();
        status = status.trim().toLowerCase();

        switch (status) {
            case "delete":
                if (!badge.getIsActive()) {
                    return APIResponse.error("Huy hiệu đã bị vô hiệu hóa trước đó");
                }
                badge.setIsActive(false);
                badgeRepository.save(badge);
                return APIResponse.success("Vô hiệu hóa huy hiệu thành công");

            case "restore":
                if (badge.getIsActive()) {
                    return APIResponse.error("Huy hiệu đang hoạt động, không cần khôi phục");
                }
                badge.setIsActive(true);
                badgeRepository.save(badge);
                return APIResponse.success("Khôi phục huy hiệu thành công");

            default:
                return APIResponse.error("Trạng thái không hợp lệ!");
        }
    }

}

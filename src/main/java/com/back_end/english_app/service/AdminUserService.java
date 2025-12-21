package com.back_end.english_app.service;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.user.AdminUserResponse;
import com.back_end.english_app.entity.LevelEntity;
import com.back_end.english_app.entity.Role;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.LevelRepository;
import com.back_end.english_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;
    private final LevelRepository levelRepository;

    // get all users with pagination
    public APIResponse<Page<AdminUserResponse>> getAllUsers(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserEntity> usersPage = userRepository.findAll(pageable);

            Page<AdminUserResponse> responsePage = usersPage.map(user -> {
                // Tính level từ totalXp
                Optional<LevelEntity> levelOpt = levelRepository.findLevelByTotalXp(user.getTotalXp() != null ? user.getTotalXp() : 0);
                Integer levelNumber = levelOpt.map(LevelEntity::getLevelNumber).orElse(1);
                String levelName = levelOpt.map(LevelEntity::getLevelName).orElse("Newbie");
                
                return new AdminUserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullname(),
                        user.getAvatar(),
                        user.getRole().name(),
                        user.getProvider(),
                        user.getTotalXp(),
                        levelNumber,
                        levelName,
                        user.getIsActive(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                );
            });

            return APIResponse.success(responsePage);

        } catch (Exception e) {
            return APIResponse.error("Lỗi truy xuất cơ sở dữ liệu: " + e.getMessage());
        }
    }

    public APIResponse<String> updatePermissionById(Long id, Role role) {
        Optional<UserEntity> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) {
            return APIResponse.error("Tài khoản không tồn tại");
        }

        if (role != Role.ADMIN && role != Role.USER) {
            return APIResponse.error("Role không hợp lệ");
        }

        UserEntity user = optUser.get();

        // Nếu role giống hiện tại -> không cần update
        if (user.getRole() == role) {
            return APIResponse.error("Người dùng đã có quyền này rồi");
        }
        user.setRole(role);
        userRepository.save(user);
        return APIResponse.success("Cập nhật quyền thành công thành: " + role.name());
    }


    public APIResponse<String> deleteOrRestoreUser(Long id, String status) {
        // Tìm user theo id
        Optional<UserEntity> optUser = userRepository.findById(id);

        if (optUser.isEmpty()) {
            return APIResponse.error("Tài khoản không tồn tại");
        }

        UserEntity user = optUser.get();

        // Chuẩn hoá status
        status = status.trim().toLowerCase();

        switch (status) {
            case "delete":
                if (!user.getIsActive()) {
                    return APIResponse.error("Tài khoản đã bị vô hiệu hóa trước đó");
                }
                user.setIsActive(false);
                userRepository.save(user);
                return APIResponse.success("Vô hiệu hóa tài khoản thành công");

            case "restore":
                if (user.getIsActive()) {
                    return APIResponse.error("Tài khoản đang hoạt động, không cần khôi phục");
                }
                user.setIsActive(true);
                userRepository.save(user);
                return APIResponse.success("Khôi phục tài khoản thành công");

            default:
                return APIResponse.error("Trạng thái không hợp lệ!");
        }
    }

    /**
     * Lấy thông tin cơ bản của user cho admin
     */
    public APIResponse<AdminUserResponse> getUserProfile(Long userId) {
        Optional<UserEntity> optUser = userRepository.findById(userId);
        if (optUser.isEmpty()) {
            return APIResponse.error("Người dùng không tồn tại");
        }

        UserEntity user = optUser.get();

        // Tìm level của user dựa trên totalXp
        Integer levelNumber = null;
        String levelName = null;
        if (user.getTotalXp() != null) {
            Optional<LevelEntity> levelOpt = levelRepository.findLevelByTotalXp(user.getTotalXp());
            if (levelOpt.isPresent()) {
                LevelEntity level = levelOpt.get();
                levelNumber = level.getLevelNumber();
                levelName = level.getLevelName();
            }
        }

        AdminUserResponse userResponse = AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .avatar(user.getAvatar())
                .role(user.getRole().name())
                .provider(user.getProvider())
                .totalXp(user.getTotalXp())
                .levelNumber(levelNumber)
                .levelName(levelName)
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        return APIResponse.success(userResponse);
    }


}

package com.back_end.english_app.service;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.respones.user.AdminUserResponse;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    public APIResponse<List<AdminUserResponse>> getAllUsers(){
        List<UserEntity> users = userRepository.findAll();
        return APIResponse.success(users.stream().map(user -> new AdminUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullname(),
                user.getAvatar(),
                user.getRole().name(),
                user.getProvider(),
                user.getTotalXp(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        )).toList());
    }

    public APIResponse<String> deleteUser(Long id){
        Optional<UserEntity> optUser = userRepository.findByIdAndIsActiveTrue(id);
        if(optUser.isEmpty()){
            return APIResponse.error("Tài khoản không tồn tại");
        }
        UserEntity user = optUser.get();
        user.setIsActive(false);
        userRepository.save(user);
        return APIResponse.success("Vô hiệu hóa thành công tài khoản");
    }
}

package com.back_end.english_app.controller.user;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.dto.request.auth.AuthRequest;
import com.back_end.english_app.dto.request.auth.RefreshTokenRequest;
import com.back_end.english_app.dto.request.auth.RegisterRequestDTO;
import com.back_end.english_app.dto.respones.auth.AuthResponse;
import com.back_end.english_app.dto.respones.auth.LevelResponse;
import com.back_end.english_app.dto.respones.auth.UserResponse;
import com.back_end.english_app.dto.respones.auth.UserStreakResponse;
import com.back_end.english_app.entity.RefreshTokenEntity;
import com.back_end.english_app.entity.Role;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.entity.LevelEntity;
import com.back_end.english_app.entity.UserStreakEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.repository.LevelRepository;
import com.back_end.english_app.security.jwt.JwtUtil;
import com.back_end.english_app.service.user.RefreshTokenService;
import com.back_end.english_app.service.user.UserStreakService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final LevelRepository levelRepository;
    private final UserStreakService userStreakService;

    @PostMapping("/register")
    public APIResponse<?> register(@RequestBody RegisterRequestDTO request) {
        log.info("Register request: email={}, username={}, fullname={}",
                request.getEmail(), request.getUsername(), request.getFullname());
        // Kiểm tra email đã tồn tại
        Optional<UserEntity> existingUser = userRepository.findByEmailAndIsActiveTrue(request.getEmail());
        if (existingUser.isPresent()) {
            log.warn("Register failed: Email {} đã tồn tại", request.getEmail());
            return APIResponse.error("Email này đã tồn tại trong cơ sở dữ liệu");
        }

        // Kiểm tra username đã tồn tại
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Register failed: Username {} đã tồn tại", request.getUsername());
            return APIResponse.error("User name đã tồn tại hãy chọn một User name mới!");
        }
        UserEntity userNew = new UserEntity();
        userNew.setEmail(request.getEmail());
        userNew.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        userNew.setUsername(request.getUsername());
        userNew.setFullname(request.getFullname());
        userNew.setProvider("LOCAL");
        userNew.setRole(Role.USER);
        userNew.setIsActive(true);

        userRepository.save(userNew);

        return APIResponse.success(userNew);
    }

    @PostMapping("/login")
    public APIResponse<AuthResponse> login(@RequestBody AuthRequest request){
        log.info("Login request: email={}",
                request.getEmail());
        Optional<UserEntity> optionalUser = userRepository.findByEmailAndIsActiveTrue(request.getEmail());
        if (optionalUser.isEmpty()) {
            return APIResponse.notFound("Người dùng không tồn tại hoặc chưa kích hoạt");
        }
        UserEntity user = optionalUser.get();


        // Authenticate trước
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Chỉ cập nhật streak sau khi authenticate thành công
        userStreakService.updateStreak(user);

        // Tạo access token
        String accessToken = jwtUtil.generateAccessToken(authentication);

        // Tạo refresh token
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(user);

        return APIResponse.success(new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getAvatar(),
                user.getFullname(),
                user.getUsername(),
                user.getRole().name(),
                user.getTotalXp(),
                user.getIsActive(),
                accessToken,
                refreshToken.getToken()
        ));
    }

    @PostMapping("/refresh")
    public APIResponse<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshToken -> {
                    // Kiểm tra refresh token có hết hạn không
                    if (!refreshTokenService.verifyExpiration(refreshToken)) {
                        return APIResponse.<AuthResponse>error("Refresh token đã hết hạn. Vui lòng đăng nhập lại!");
                    }

                    UserEntity user = refreshToken.getUser();

                    // Tạo access token mới
                    String newAccessToken = jwtUtil.generateAccessTokenFromEmail(
                            user.getId(),
                            user.getEmail(),
                            "ROLE_" + user.getRole().name()
                    );

                    // Tạo refresh token mới
                    RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user);

                    return APIResponse.success(new AuthResponse(
                            user.getId(),
                            user.getEmail(),
                            user.getAvatar(),
                            user.getFullname(),
                            user.getUsername(),
                            user.getRole().name(),
                            user.getTotalXp(),
                            user.getIsActive(),
                            newAccessToken,
                            newRefreshToken.getToken()
                    ));
                })
                .orElseGet(() -> APIResponse.error("Refresh token không tồn tại!"));
    }

    @PostMapping("/logout")
    public APIResponse<?> logout(@RequestBody RefreshTokenRequest request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return APIResponse.success("Đăng xuất thành công!");
    }

    @GetMapping("/me")
    public APIResponse<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return APIResponse.error("Unauthorized");
        }

        String email = authentication.getName();
        UserEntity user = userRepository.findByEmailAndIsActiveTrue(email).orElseThrow();

        // Tìm level của user dựa trên totalXp
        LevelResponse levelResponse = null;
        if (user.getTotalXp() != null) {
            Optional<LevelEntity> levelOpt = levelRepository.findLevelByTotalXp(user.getTotalXp());
            if (levelOpt.isPresent()) {
                LevelEntity level = levelOpt.get();
                levelResponse = LevelResponse.builder()
                        .levelNumber(level.getLevelNumber())
                        .levelName(level.getLevelName())
                        .minXp(level.getMinXp())
                        .maxXp(level.getMaxXp())
                        .description(level.getDescription())
                        .iconUrl(level.getIconUrl())
                        .build();
            }
        }

        // Lấy thông tin streak của user (luôn trả về, không bao giờ null)
        UserStreakEntity streak = userStreakService.getUserStreak(user.getId());
        UserStreakResponse streakResponse = UserStreakResponse.builder()
                .currentStreak(streak.getCurrentStreak())
                .longestStreak(streak.getLongestStreak())
                .lastActivityDate(streak.getLastActivityDate())
                .streakStartDate(streak.getStreakStartDate())
                .longestStreakDate(streak.getLongestStreakDate())
                .totalStudyDays(streak.getTotalStudyDays())
                .build();

        return APIResponse.success(new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullname(),
                user.getAvatar(),
                user.getRole().name(),
                user.getProvider(),
                user.getGoogleId(),
                user.getFacebookId(),
                user.getTotalXp(),
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                levelResponse,
                streakResponse
        ));
    }
}


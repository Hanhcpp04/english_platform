package com.back_end.english_app.security.oauth2;

import com.back_end.english_app.entity.RefreshTokenEntity;
import com.back_end.english_app.entity.Role;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.security.jwt.JwtUtil;
import com.back_end.english_app.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Log toàn bộ attributes để debug (bao gồm key names như "sub", "email", "picture")
        Map<String, Object> attrs = oAuth2User.getAttributes();
        log.info("OAuth2 attributes: {}", attrs);

        String email = (String) attrs.get("email");
        if (email == null) {
            // Một số provider trả sub là id, hoặc mappings khác — in ra để kiểm tra
            log.warn("Email attribute is null. Attributes keys: {}", attrs.keySet());
            // Fall back: try 'sub' if it's actually email in your token
            Object sub = attrs.get("sub");
            if (sub != null) {
                email = sub.toString();
                log.info("Fallback: using 'sub' as email: {}", email);
            }
        }

        // Lấy tên và avatar từ thông tin Google (Google thường trả về thuộc tính "picture")
        String name = (String) attrs.get("name");
        String googleId = attrs.get("sub") != null ? attrs.get("sub").toString() : null;
        String picture = (String) attrs.get("picture");

        // Tìm hoặc tạo user. LƯU Ý: không gọi find...orElseThrow trước vì nếu user chưa có sẽ throw và chặn flow
        UserEntity user = null;
        if (email != null) {
            try {
                Optional<UserEntity> maybe = userRepository.findByEmail(email);
                if (maybe.isPresent()) {
                    user = maybe.get();
                    boolean changed = false;
                    if (user.getIsActive() == null || !user.getIsActive()) {
                        user.setIsActive(true);
                        changed = true;
                    }
                    if (user.getGoogleId() == null && googleId != null) {
                        user.setGoogleId(googleId);
                        changed = true;
                    }
                    if (picture != null && !picture.equals(user.getAvatar())) {
                        log.info("Updating avatar for existing user id={} from={} to={}", user.getId(), user.getAvatar(), picture);
                        user.setAvatar(picture);
                        changed = true;
                    }
                    if (changed) {
                        UserEntity updated = userRepository.saveAndFlush(user);
                        log.info("Updated existing user id={} email={} avatar={}", updated.getId(), updated.getEmail(), updated.getAvatar());
                    } else {
                        log.info("Existing user id={} email={} - no changes", user.getId(), user.getEmail());
                    }
                } else {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(email);
                    newUser.setUsername(name != null ? name : email);
                    newUser.setFullname(name != null ? name : email);
                    newUser.setProvider("GOOGLE");
                    newUser.setGoogleId(googleId);
                    newUser.setRole(Role.USER);
                    newUser.setIsActive(true);
                    if (picture != null) {
                        newUser.setAvatar(picture);
                    }
                    UserEntity saved = userRepository.saveAndFlush(newUser);
                    log.info("Created new user id={} email={} avatar={}", saved.getId(), saved.getEmail(), saved.getAvatar());
                    user = saved;
                }
            } catch (DataAccessException e) {
                log.error("Failed to persist user for email={}. Exception: {}", email, e.toString());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to persist user");
                return;
            }
        } else {
            // Email vẫn null -> không thể lookup user bằng email. Log và throw để dev biết.
            log.error("Cannot proceed with OAuth2 login: email is null in attributes: {}", attrs);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Missing email in OAuth2 attributes");
            return;
        }

        // Ensure avatar persisted (re-fetch and log)
        Optional<UserEntity> check = userRepository.findById(user.getId());
        check.ifPresent(u -> log.info("DB check: user id={} avatar={}", u.getId(), u.getAvatar()));

        // Tạo access token (kèm avatar nếu có)
        String accessToken = jwtUtil.generateAccessTokenFromEmail(user.getId(), email, "ROLE_" + user.getRole().name(), user.getAvatar());

        // Tạo refresh token — bọc try/catch để tránh rollback toàn bộ nếu tạo refresh token lỗi
        RefreshTokenEntity refreshToken = null;
        try {
            refreshToken = refreshTokenService.createRefreshToken(user);
        } catch (Exception e) {
            log.error("Failed to create refresh token for user id={}. Exception: {}", user.getId(), e.toString());
        }

        // Redirect về frontend với access token, refresh token và avatar (nếu có)
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://localhost:5173/oauth2/redirect")
                .queryParam("accessToken", accessToken);
        if (refreshToken != null) {
            builder.queryParam("refreshToken", refreshToken.getToken());
        }
        if (user.getAvatar() != null) {
            builder.queryParam("avatar", user.getAvatar());
        }

        String targetUrl = builder.build().toUriString();
        log.info("Redirecting to frontend: {}", targetUrl);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
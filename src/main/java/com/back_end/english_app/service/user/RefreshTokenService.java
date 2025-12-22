package com.back_end.english_app.service.user;

import com.back_end.english_app.entity.RefreshTokenEntity;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.RefreshTokenRepository;
import com.back_end.english_app.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        // Xóa refresh token cũ của user (nếu có)
        refreshTokenRepository.deleteByUser(user);

        // Tạo refresh token mới
        String tokenString = jwtUtil.generateRefreshToken();
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setUser(user);
        refreshToken.setToken(tokenString);
        refreshToken.setExpiryDate(expiryDate);
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setIsActive(true);

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByTokenAndIsActiveTrue(token);
    }

    public boolean verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            token.setIsActive(false);
            refreshTokenRepository.save(token);
            return false;
        }
        return true;
    }

    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    @Transactional
    public void deleteByUser(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);
    }
}


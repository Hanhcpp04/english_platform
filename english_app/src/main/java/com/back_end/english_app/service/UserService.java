package com.back_end.english_app.service;

import com.back_end.english_app.dto.AuthResponse;
import com.back_end.english_app.dto.LoginRequest;
import com.back_end.english_app.dto.RegisterRequest;
import com.back_end.english_app.entity.EmailVerificationTokenEntity;
import com.back_end.english_app.entity.RoleEntity;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.EmailVerificationTokenRepository;
import com.back_end.english_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public String register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        // Create new user
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullname(request.getFullname());
        user.setRole(RoleEntity.USER);
        user.setIsActive(true);
        user.setIsEmailVerified(false);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));

        // Save user
        user = userRepository.save(user);

        // Create verification token entity
        EmailVerificationTokenEntity verificationToken = new EmailVerificationTokenEntity();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        // Send verification email
        try {
            emailService.sendVerificationEmail(user, token);
        } catch (Exception e) {
            log.error("Failed to send verification email", e);
            // Don't fail registration if email sending fails
        }

        return "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.";
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmailOrUsername(),
                        request.getPassword()
                )
        );

        // Load user details
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmailOrUsername());
        
        // Find user entity
        UserEntity user = userRepository.findByEmail(request.getEmailOrUsername())
                .orElseGet(() -> userRepository.findByUsername(request.getEmailOrUsername())
                        .orElseThrow(() -> new RuntimeException("User not found")));

        // Generate JWT token
        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .role(user.getRole().name())
                .emailVerified(user.getIsEmailVerified())
                .build();
    }

    @Transactional
    public String verifyEmail(String token) {
        EmailVerificationTokenEntity verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token xác thực không hợp lệ"));

        if (verificationToken.isExpired()) {
            throw new RuntimeException("Token xác thực đã hết hạn");
        }

        if (verificationToken.isConfirmed()) {
            throw new RuntimeException("Email đã được xác thực trước đó");
        }

        // Update user
        UserEntity user = verificationToken.getUser();
        user.setIsEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        userRepository.save(user);

        // Update token
        verificationToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        return "Xác thực email thành công!";
    }

    @Transactional
    public String resendVerificationEmail(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với email này"));

        if (user.getIsEmailVerified()) {
            throw new RuntimeException("Email đã được xác thực");
        }

        // Generate new token
        String token = UUID.randomUUID().toString();
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        // Delete old tokens and create new one
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        EmailVerificationTokenEntity verificationToken = new EmailVerificationTokenEntity();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        tokenRepository.save(verificationToken);

        // Send verification email
        emailService.sendVerificationEmail(user, token);

        return "Email xác thực đã được gửi lại!";
    }
}

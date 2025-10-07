package com.back_end.english_app.service;

import com.back_end.english_app.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(UserEntity user, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Xác thực tài khoản - English Learning Platform");
            
            String verificationLink = baseUrl + "/api/auth/verify-email?token=" + token;
            
            String emailContent = String.format(
                "Xin chào %s,\n\n" +
                "Cảm ơn bạn đã đăng ký tài khoản tại English Learning Platform!\n\n" +
                "Để hoàn tất quá trình đăng ký, vui lòng click vào link dưới đây để xác thực email:\n\n" +
                "%s\n\n" +
                "Link này sẽ hết hạn sau 24 giờ.\n\n" +
                "Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "English Learning Platform Team",
                user.getFullname(),
                verificationLink
            );
            
            message.setText(emailContent);
            mailSender.send(message);
            
            log.info("Verification email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(UserEntity user, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(user.getEmail());
            message.setSubject("Đặt lại mật khẩu - English Learning Platform");
            
            String resetLink = baseUrl + "/api/auth/reset-password?token=" + token;
            
            String emailContent = String.format(
                "Xin chào %s,\n\n" +
                "Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.\n\n" +
                "Để đặt lại mật khẩu, vui lòng click vào link dưới đây:\n\n" +
                "%s\n\n" +
                "Link này sẽ hết hạn sau 1 giờ.\n\n" +
                "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                "Trân trọng,\n" +
                "English Learning Platform Team",
                user.getFullname(),
                resetLink
            );
            
            message.setText(emailContent);
            mailSender.send(message);
            
            log.info("Password reset email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", user.getEmail(), e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }
}

package com.back_end.english_app.controller;

import com.back_end.english_app.dto.ApiResponse;
import com.back_end.english_app.dto.AuthResponse;
import com.back_end.english_app.dto.LoginRequest;
import com.back_end.english_app.dto.RegisterRequest;
import com.back_end.english_app.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "API xác thực người dùng")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản mới")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String message = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(message));
        } catch (RuntimeException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Registration error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi trong quá trình đăng ký"));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
        } catch (RuntimeException e) {
            log.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Email/Username hoặc mật khẩu không chính xác"));
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi trong quá trình đăng nhập"));
        }
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Xác thực email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        try {
            String message = userService.verifyEmail(token);
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (RuntimeException e) {
            log.error("Email verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Email verification error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi trong quá trình xác thực email"));
        }
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Gửi lại email xác thực")
    public ResponseEntity<ApiResponse<String>> resendVerificationEmail(@RequestParam String email) {
        try {
            String message = userService.resendVerificationEmail(email);
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (RuntimeException e) {
            log.error("Resend verification failed: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Resend verification error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi khi gửi lại email xác thực"));
        }
    }

    @GetMapping("/health")
    @Operation(summary = "Kiểm tra trạng thái API")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Authentication API is running"));
    }
}

package com.back_end.english_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {
    
    @NotBlank(message = "Email hoặc username không được để trống")
    String emailOrUsername;
    
    @NotBlank(message = "Mật khẩu không được để trống")
    String password;
}

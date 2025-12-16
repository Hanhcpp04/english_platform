package com.back_end.english_app.dto.respones.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String email;
    private String avatar;
    private String fullName;
    private String name;
    private String role;
    private Integer totalXp;
    private boolean isActive;
    private String accessToken;
    private String refreshToken;
}

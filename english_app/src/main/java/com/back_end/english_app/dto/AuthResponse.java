package com.back_end.english_app.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    
    String accessToken;
    @Builder.Default
    String tokenType = "Bearer";
    Long userId;
    String username;
    String email;
    String fullname;
    String role;
    boolean emailVerified;
}

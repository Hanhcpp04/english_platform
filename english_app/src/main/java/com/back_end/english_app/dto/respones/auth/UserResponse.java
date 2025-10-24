package com.back_end.english_app.dto.respones.auth;

import com.back_end.english_app.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    Long id;
    String username;
    String email;
    String fullname;
    String avatar;
    String role;
    String provider;
    String googleId;
    String facebookId;
    Integer totalXp;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

}

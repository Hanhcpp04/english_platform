package com.back_end.english_app.dto.respones.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class AdminUserResponse {
    Long id;
    String username;
    String email;
    String fullname;
    String avatar;
    String role;
    String provider;
    Integer totalXp;
    Integer levelNumber;
    String levelName;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

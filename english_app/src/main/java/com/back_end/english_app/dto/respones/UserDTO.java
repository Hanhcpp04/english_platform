package com.back_end.english_app.dto.respones;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
        private Long id;
        private String username;
        private String email;
}

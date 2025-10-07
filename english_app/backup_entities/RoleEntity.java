package com.back_end.english_app.entity;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Entity

@FieldDefaults( level = AccessLevel.PRIVATE)
public enum RoleEntity {
        ADMIN,
        USER
}

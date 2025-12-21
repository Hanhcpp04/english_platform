package com.back_end.english_app.entity;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults( level = AccessLevel.PRIVATE)
public enum Role {
        ADMIN,
        USER
}

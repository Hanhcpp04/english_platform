package com.back_end.english_app.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "badges")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BadgeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    String description;

    @Column(name = "icon_url", length = 500)
    String iconUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    ConditionType conditionType;

    @Column(name = "condition_value", nullable = false)
    Integer conditionValue;

    @Column(name = "xp_reward", columnDefinition = "INT DEFAULT 0")
    Integer xpReward = 0;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
}

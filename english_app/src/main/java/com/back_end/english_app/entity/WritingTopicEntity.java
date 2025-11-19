package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "writing_categories",
        indexes = {
                @Index(name = "idx_active", columnList = "is_active")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritingCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String writingTips;

    @Column(name = "xp_reward")
    private Integer xpReward;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

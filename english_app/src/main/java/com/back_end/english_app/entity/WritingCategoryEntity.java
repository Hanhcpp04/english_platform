package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "writing_categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritingCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "xp_reward")
    private Integer xpReward = 50;

    @Column(name = "writing_tips", columnDefinition = "TEXT")
    private String writingTips;

    @Column(name = "sample_prompt", columnDefinition = "TEXT")
    private String samplePrompt;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WritingPromptEntity> prompts;
}

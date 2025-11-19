package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "writing_",
        indexes = {
                @Index(name = "idx_user_mode", columnList = "user_id, mode"),
                @Index(name = "idx_completed", columnList = "is_completed")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritingPromptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User quan hệ Many-to-One
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private WritingCategoryEntity writingForm;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PROMPT', 'FREE') default 'PROMPT'")
    private Mode mode;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String userContent;

    private Integer wordCount;

    private Integer grammarScore;
    private Integer vocabularyScore;
    private Integer coherenceScore;
    private Integer overallScore;

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(columnDefinition = "JSON")
    private String grammarSuggestions;

    @Column(columnDefinition = "JSON")
    private String vocabularySuggestions;

    private Integer timeSpent;

    @Column(name = "xp_reward")
    private Integer xpReward;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) this.isActive = true;
        if (this.isCompleted == null) this.isCompleted = false;
        if (this.mode == null) this.mode = Mode.PROMPT;
    }
    public enum Mode {
        PROMPT, FREE
    }
}

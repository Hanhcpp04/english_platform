package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "writing_prompts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritingPromptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private WritingCategoryEntity category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String prompt;

    @Column(name = "user_content", columnDefinition = "TEXT")
    private String userContent;

    @Column(name = "word_count")
    private Integer wordCount = 0;

    @Column(name = "grammar_score")
    private Integer grammarScore;

    @Column(name = "vocabulary_score")
    private Integer vocabularyScore;

    @Column(name = "coherence_score")
    private Integer coherenceScore;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "grammar_suggestions", columnDefinition = "JSON")
    private String grammarSuggestions;

    @Column(name = "vocabulary_suggestions", columnDefinition = "JSON")
    private String vocabularySuggestions;

    @Column(name = "time_spent")
    private Integer timeSpent;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "xp_reward")
    private Integer xpReward = 50;

    @Column(name = "is_completed")
    private Boolean isCompleted = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

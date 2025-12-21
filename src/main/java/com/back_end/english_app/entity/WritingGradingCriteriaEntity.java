package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "writing_grading_criteria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WritingGradingCriteriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "task_id", nullable = false, unique = true)
    private WrittingTaskEntity task;

    @Column(name = "grammar_weight", nullable = false)
    private Integer grammarWeight = 30; // %

    @Column(name = "vocabulary_weight", nullable = false)
    private Integer vocabularyWeight = 30; // %

    @Column(name = "coherence_weight", nullable = false)
    private Integer coherenceWeight = 40; // %

    @Column(name = "min_word_count")
    private Integer minWordCount = 100;

    @Column(name = "max_word_count")
    private Integer maxWordCount = 500;

    @Column(columnDefinition = "TEXT")
    private String customInstructions; // Hướng dẫn chấm điểm tùy chỉnh cho AI

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_daily_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDailyStatsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "vocab_learned")
    private Integer vocabLearned = 0;

    @Column(name = "grammar_completed")
    private Integer grammarCompleted = 0;

    @Column(name = "exercises_done")
    private Integer exercisesDone = 0;

    @Column(name = "writing_submitted")
    private Integer writingSubmitted = 0;

    @Column(name = "forum_posts")
    private Integer forumPosts = 0;

    @Column(name = "forum_comments")
    private Integer forumComments = 0;

    @Column(name = "study_time_minutes")
    private Integer studyTimeMinutes = 0;

    @Column(name = "xp_earned")
    private Integer xpEarned = 0;

    @Column(name = "accuracy_rate", precision = 5, scale = 2)
    private BigDecimal accuracyRate;

    @Column(name = "is_study_day")
    private Boolean isStudyDay = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}

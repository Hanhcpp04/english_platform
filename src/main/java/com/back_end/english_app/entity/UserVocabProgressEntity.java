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
@Table(name = "user_vocab_progress")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVocabProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    VocabTopicEntity topic;

    @ManyToOne
    @JoinColumn(name = "word_id")
    VocabWordEntity word;

    @ManyToOne
    @JoinColumn(name = "question_id")
    VocabExerciseQuestionEntity question;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, columnDefinition = "ENUM('flashcard', 'exercise', 'review')")
    AcitvityType activityType;

    @Column(name = "is_completed", columnDefinition = "BOOLEAN DEFAULT FALSE")
    Boolean isCompleted = false;

    @Column(columnDefinition = "INT CHECK (score >= 0 AND score <= 100)")
    Integer score;

    @Column(columnDefinition = "INT DEFAULT 1 CHECK (attempts > 0)")
    Integer attempts = 1;

    @Column(name = "time_spent")
    Integer timeSpent;

    @Column(name = "completed_at")
    LocalDateTime completedAt;

    @Column(name = "last_reviewed")
    LocalDateTime lastReviewed;

    @Column(name = "next_review")
    LocalDateTime nextReview;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
}
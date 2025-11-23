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
@Table(name = "vocab_exercise_questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabExerciseQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    VocabExerciseTypeEntity type;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    VocabTopicEntity topic;

    @Column(nullable = false, columnDefinition = "TEXT")
    String question;

    @Column(columnDefinition = "JSON")
    String options;

    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    String correctAnswer;

    @Column(columnDefinition = "TEXT")
    String explanation;

    @Column(name = "xp_reward", columnDefinition = "INT DEFAULT 5")
    Integer xpReward = 5;

    @Column(name = "is_active")
    Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
}

package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ lịch sử câu trả lời của user cho bài tập từ vựng
 * Tương tự UserGrammarExerciseAnswerEntity
 */
@Entity
@Table(name = "user_vocab_exercise_answers",
        indexes = {
                @Index(name = "idx_vocab_user_question", columnList = "user_id, question_id"),
                @Index(name = "idx_vocab_user_correct", columnList = "user_id, is_correct"),
                @Index(name = "idx_vocab_user_type_topic", columnList = "user_id, type_id, topic_id")
        })
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVocabExerciseAnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    VocabExerciseQuestionEntity question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    VocabExerciseTypeEntity type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    VocabTopicEntity topic;

    @Column(name = "user_answer", columnDefinition = "TEXT", nullable = false)
    String userAnswer;

    @Column(name = "is_correct", nullable = false)
    Boolean isCorrect;

    @Column(name = "xp_earned", columnDefinition = "INT DEFAULT 0")
    Integer xpEarned = 0;

    @Column(name = "attempted_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    LocalDateTime attemptedAt;
}


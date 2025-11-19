package com.back_end.english_app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_grammar_exercise_answers",
        indexes = {
                @Index(name = "idx_user_question", columnList = "user_id, question_id"),
                @Index(name = "idx_user_correct", columnList = "user_id, is_correct"),
                @Index(name = "idx_user_correct", columnList = "user_id, is_correct")
        })
public class UserGrammarExerciseAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private GrammarQuestionEntity question;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ExerciseGrammarTypeEntity type;

    @Column(name = "user_answer", columnDefinition = "TEXT", nullable = false)
    private String userAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "attempted_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime attemptedAt;
}

package com.englishplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_results")
public class QuizResult {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "score")
    private Integer score;
    
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    @Column(name = "percentage")
    private Double percentage;
    
    @Column(name = "time_taken_minutes")
    private Integer timeTakenMinutes;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    // Constructors
    public QuizResult() {
        this.completedAt = LocalDateTime.now();
    }
    
    public QuizResult(User user, Quiz quiz, Integer score, Integer totalQuestions) {
        this();
        this.user = user;
        this.quiz = quiz;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = (double) score / totalQuestions * 100;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { 
        this.score = score;
        if (this.totalQuestions != null && this.totalQuestions > 0) {
            this.percentage = (double) score / totalQuestions * 100;
        }
    }
    
    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { 
        this.totalQuestions = totalQuestions;
        if (this.score != null && totalQuestions > 0) {
            this.percentage = (double) score / totalQuestions * 100;
        }
    }
    
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    
    public Integer getTimeTakenMinutes() { return timeTakenMinutes; }
    public void setTimeTakenMinutes(Integer timeTakenMinutes) { this.timeTakenMinutes = timeTakenMinutes; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
}
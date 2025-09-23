package com.englishplatform.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress")
public class UserProgress {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "words_learned")
    private Integer wordsLearned = 0;
    
    @Column(name = "quizzes_completed")
    private Integer quizzesCompleted = 0;
    
    @Column(name = "total_score")
    private Integer totalScore = 0;
    
    @Column(name = "average_score")
    private Double averageScore = 0.0;
    
    @Column(name = "current_level")
    @Enumerated(EnumType.STRING)
    private Level currentLevel = Level.BEGINNER;
    
    @Column(name = "experience_points")
    private Integer experiencePoints = 0;
    
    @Column(name = "last_activity")
    private LocalDateTime lastActivity;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    // Constructors
    public UserProgress() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    public UserProgress(User user) {
        this();
        this.user = user;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
    
    // Methods for updating progress
    public void addQuizResult(Integer score, Integer totalQuestions) {
        this.quizzesCompleted++;
        this.totalScore += score;
        this.averageScore = (double) this.totalScore / this.quizzesCompleted;
        
        // Add experience points
        int earnedPoints = (score * 10) + (score == totalQuestions ? 20 : 0); // Bonus for perfect score
        this.experiencePoints += earnedPoints;
        
        // Update level based on experience points
        updateLevel();
    }
    
    public void addWordsLearned(int count) {
        this.wordsLearned += count;
        this.experiencePoints += count * 5; // 5 points per word learned
        updateLevel();
    }
    
    private void updateLevel() {
        if (experiencePoints >= 1000) {
            currentLevel = Level.ADVANCED;
        } else if (experiencePoints >= 500) {
            currentLevel = Level.INTERMEDIATE;
        } else {
            currentLevel = Level.BEGINNER;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getWordsLearned() { return wordsLearned; }
    public void setWordsLearned(Integer wordsLearned) { this.wordsLearned = wordsLearned; }
    
    public Integer getQuizzesCompleted() { return quizzesCompleted; }
    public void setQuizzesCompleted(Integer quizzesCompleted) { this.quizzesCompleted = quizzesCompleted; }
    
    public Integer getTotalScore() { return totalScore; }
    public void setTotalScore(Integer totalScore) { this.totalScore = totalScore; }
    
    public Double getAverageScore() { return averageScore; }
    public void setAverageScore(Double averageScore) { this.averageScore = averageScore; }
    
    public Level getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(Level currentLevel) { this.currentLevel = currentLevel; }
    
    public Integer getExperiencePoints() { return experiencePoints; }
    public void setExperiencePoints(Integer experiencePoints) { this.experiencePoints = experiencePoints; }
    
    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public enum Level {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}
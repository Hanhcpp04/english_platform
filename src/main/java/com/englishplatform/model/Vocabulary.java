package com.englishplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vocabulary")
public class Vocabulary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 100)
    @Column(unique = true)
    private String word;
    
    @NotBlank
    @Size(max = 500)
    private String definition;
    
    @Size(max = 200)
    private String pronunciation;
    
    @Size(max = 1000)
    private String example;
    
    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty = DifficultyLevel.BEGINNER;
    
    @Enumerated(EnumType.STRING)
    private WordType wordType;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "vocabulary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<QuizQuestion> quizQuestions = new HashSet<>();
    
    // Constructors
    public Vocabulary() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Vocabulary(String word, String definition) {
        this();
        this.word = word;
        this.definition = definition;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }
    
    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }
    
    public String getPronunciation() { return pronunciation; }
    public void setPronunciation(String pronunciation) { this.pronunciation = pronunciation; }
    
    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
    
    public DifficultyLevel getDifficulty() { return difficulty; }
    public void setDifficulty(DifficultyLevel difficulty) { this.difficulty = difficulty; }
    
    public WordType getWordType() { return wordType; }
    public void setWordType(WordType wordType) { this.wordType = wordType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public Set<QuizQuestion> getQuizQuestions() { return quizQuestions; }
    public void setQuizQuestions(Set<QuizQuestion> quizQuestions) { this.quizQuestions = quizQuestions; }
    
    public enum DifficultyLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
    
    public enum WordType {
        NOUN, VERB, ADJECTIVE, ADVERB, PREPOSITION, CONJUNCTION, INTERJECTION
    }
}
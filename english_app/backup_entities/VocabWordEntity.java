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
@Table(name = "vocab_word")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VocabWordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    VocabTopicEntity topic;

    @Column(name = "english_word", nullable = false)
    String englishWord;

    @Column(name = "vietnamese_meaning", nullable = false, columnDefinition = "TEXT")
    String vietnameseMeaning;

    String pronunciation;

    @Column(name = "audio_url", length = 500)
    String audioUrl;

    @Column(name = "image_url", length = 500)
    String imageUrl;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    String exampleSentence;

    @Column(name = "example_translation", columnDefinition = "TEXT")
    String exampleTranslation;

    @Column(name = "word_type", length = 100)
    String wordType;

    @Column(name = "xp_reward", columnDefinition = "INT DEFAULT 3")
    Integer xpReward = 3;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;

}
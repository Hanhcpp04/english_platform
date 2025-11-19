package com.back_end.english_app.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "writing_task")
public class WrittingTaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;
    @Column(name = "writing_tips", columnDefinition = "TEXT")
    private String writingTips;
    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward = 50;

    @Column(name = "is_active")
    private Boolean isActive = true;
    @Column(name = "is_completed")
    private Boolean isCompleted;
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private WritingTopicEntity writingTopic;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

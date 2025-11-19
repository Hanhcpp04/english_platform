package com.back_end.english_app.dto.respones.grammar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteLessonResponseDTO {
    private Long progressId;
    private Long userId;
    private Long topicId;
    private Long lessonId;
    private String type;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private String message;
    private Integer xpAwarded;      // XP được thưởng cho lần hoàn thành này
    private Integer totalXp;        // Tổng XP hiện tại của user
}


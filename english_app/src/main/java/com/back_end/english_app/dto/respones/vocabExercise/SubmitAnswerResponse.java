package com.back_end.english_app.dto.respones.vocabExercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerResponse {
    private Boolean isCorrect;
    private String correctAnswer;
    private Integer xpEarned;
    private Integer totalXp;
    private String explanation;
    private ProgressDetail progress;
    private Boolean isAlreadyCompleted;  // ✅ NEW: Cho biết câu đã hoàn thành hay chưa
}
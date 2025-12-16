package com.back_end.english_app.dto.respones.vocabExercise;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Integer id;
    private String question;
    private String questionVi;
    private JsonNode options;
    private String correctAnswer;
    private Integer correctIndex;
    private Integer xpReward;
    private Boolean isCompleted;
    // For Word Arrangement
    private String[] scrambledLetters;
    private String correctWord;
    private String hint;
}

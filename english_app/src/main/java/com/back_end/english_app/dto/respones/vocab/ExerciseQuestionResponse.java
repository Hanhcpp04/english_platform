package com.back_end.english_app.dto.respones.vocab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseQuestionResponse {
    private Long id;
    private Long typeId;
    private String typeName;
    private Long wordId;
    private String word;
    private String question;
    private String questionVi;
    private List<String> options; // For multiple-choice
    private Integer correctAnswer; // Index for multiple-choice
    private String correctWord; // For word-arrangement
    private String hint; // For word-arrangement
    private String hintVi; // For word-arrangement
    private List<String> scrambledLetters; // For word-arrangement
    private Integer xpReward;
    private Boolean isCompleted;
    private String explanation;
}


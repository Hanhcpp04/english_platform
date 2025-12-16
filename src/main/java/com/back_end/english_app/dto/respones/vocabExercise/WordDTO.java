package com.back_end.english_app.dto.respones.vocabExercise;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WordDTO {
    private String english;
    private String vietnamese;
    private String imageUrl;
    private String audioUrl;
}

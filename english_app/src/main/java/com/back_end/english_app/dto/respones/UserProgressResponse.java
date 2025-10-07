package com.back_end.english_app.dto.respones;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class UserProgressResponse {
    Integer totalWordsLearned;
    Integer totalTopicsCompleted;
    Integer totalXpEarned;
}

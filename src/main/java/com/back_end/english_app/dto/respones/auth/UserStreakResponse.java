package com.back_end.english_app.dto.respones.auth;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserStreakResponse {
    Integer currentStreak;
    Integer longestStreak;
    LocalDate lastActivityDate;
    LocalDate streakStartDate;
    LocalDate longestStreakDate;
    Integer totalStudyDays;
}


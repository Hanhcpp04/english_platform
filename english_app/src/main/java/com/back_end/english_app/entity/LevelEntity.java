package com.back_end.english_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table( name = "level")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults( level = AccessLevel.PRIVATE)
public class LevelEntity {
    @Id
    @Column(name = "level_number")
    Integer levelNumber;

    @Column(name = "level_name", nullable = false, length = 100)
    String levelName;

    @Column(name = "min_xp", nullable = false)
    Integer minXp;

    @Column(name = "max_xp")
    Integer maxXp;

    @Column(columnDefinition = "TEXT")
    String description;

    @Column(name = "icon_url", length = 500)
    String iconUrl;
}

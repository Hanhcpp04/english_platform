package com.back_end.english_app.dto.respones.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarQuestionDTO {

    private Long id;

    private String question;

    private String options; // JSON string of options array

    @JsonProperty("type_id")
    private Long typeId;

    @JsonProperty("xp_reward")
    private Integer xpReward;
}


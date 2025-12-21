package com.back_end.english_app.dto.request.writing;

import lombok.Data;

@Data
public class AdminWritingTaskRequest {
    private Integer topicId;
    private String question;
    private String writingTips;
    private Integer xpReward = 50;
    private Boolean isActive = true;
}

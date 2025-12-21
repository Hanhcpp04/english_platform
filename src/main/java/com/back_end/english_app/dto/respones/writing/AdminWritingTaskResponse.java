package com.back_end.english_app.dto.respones.writing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminWritingTaskResponse {
    private Integer id;
    private Integer topicId;
    private String topicName;
    private String question;
    private String writingTips;
    private Integer xpReward;
    private Boolean isActive;
    private AdminGradingCriteriaResponse gradingCriteria;
    private Integer totalSubmissions;
    private String createdAt;
}

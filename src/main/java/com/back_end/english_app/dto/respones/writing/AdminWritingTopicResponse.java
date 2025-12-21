package com.back_end.english_app.dto.respones.writing;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminWritingTopicResponse {
    private Integer id;
    private String name;
    private Boolean isActive;
    private Integer totalTasks;
    private String createdAt;
}

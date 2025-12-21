package com.back_end.english_app.dto.request.writing;

import lombok.Data;

@Data
public class AdminWritingTopicRequest {
    private String name;
    private Boolean isActive = true;
}

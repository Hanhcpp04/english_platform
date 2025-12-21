package com.back_end.english_app.dto.respones.forum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResponse {
    private Long id;
    private String mediaType;
    private String fileName;
    private String mimeType;
    private Integer fileSize;
    private String url;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
}


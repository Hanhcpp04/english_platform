package com.back_end.english_app.dto.request.forum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {
    private String content;
    private List<String> tags;
    private List<MultipartFile> images;
    private List<MultipartFile> files;
    private List<Long> existingMediaIds;
    private List<Long> removedMediaIds;
}


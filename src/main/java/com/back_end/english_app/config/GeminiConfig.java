package com.back_end.english_app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gemini")
@Data
public class GeminiConfig {
    private String apiKey;
    private String apiUrl = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent";
    private Integer maxTokens = 2048;
    private Double temperature = 0.7;
}

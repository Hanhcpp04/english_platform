package com.back_end.english_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tự động xác định đường dẫn dựa trên thư mục hiện tại của project
        String uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir)
                .toAbsolutePath()
                .toString()
                .replace("\\", "/") + "/";
        
        // Windows cần file:/// (3 dấu /), Linux/Mac cần file:// (2 dấu /)
        String resourceLocation = "file:///" + uploadPath;
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }
}



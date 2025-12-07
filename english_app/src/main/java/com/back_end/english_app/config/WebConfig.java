package com.back_end.english_app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Phải dùng file:/// (3 dấu /) cho Windows
        // Map /uploads/** to the actual file system location
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///D:/DoAnChuyenNganh/EnglishSmartBE/english_app/uploads/");
    }
}



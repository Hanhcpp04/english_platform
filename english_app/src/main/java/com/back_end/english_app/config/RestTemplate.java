package com.back_end.english_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestTemplate {
    // dùng để gọi đến các api từ bên ngoài
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

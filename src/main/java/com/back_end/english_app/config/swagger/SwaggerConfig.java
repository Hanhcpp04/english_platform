package com.back_end.english_app.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {
    @Bean
    public OpenAPI englishSmartOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EnglishSmart API")
                        .description("API backend cho web EnglishSmart")
                        .version("v0.1"));
    }
}

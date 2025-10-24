package com.back_end.english_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.back_end.english_app.repository")
@EntityScan(basePackages = "com.back_end.english_app.entity")
public class EnglishAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnglishAppApplication.class, args);
	}
}

package com.back_end.english_app;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EnglishAppApplication {

	public static void main(String[] args) {
		// Load .env file
		try {
			Dotenv dotenv = Dotenv.configure()
					.directory("./")
					.ignoreIfMissing()
					.load();
			
			// Set environment variables from .env
			dotenv.entries().forEach(entry -> {
				System.setProperty(entry.getKey(), entry.getValue());
			});
		} catch (Exception e) {
			System.err.println("Warning: Could not load .env file: " + e.getMessage());
		}
		
		SpringApplication.run(EnglishAppApplication.class, args);
	}
}

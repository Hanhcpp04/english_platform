package com.back_end.english_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnglishAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EnglishAppApplication.class, args);
		System.out.println("Hello đây sẽ là back end cho web tiếng anh của chúng ta !");
	}
}

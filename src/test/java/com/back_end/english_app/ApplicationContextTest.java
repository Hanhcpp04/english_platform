package com.back_end.english_app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple integration test to verify Spring Boot context loads successfully
 */
@SpringBootTest
@DisplayName("Application Context Tests")
class ApplicationContextTest {

    @Test
    @DisplayName("Should load application context successfully")
    void contextLoads() {
        // This test passes if the Spring application context loads without errors
        assertTrue(true, "Application context loaded successfully");
    }

    @Test
    @DisplayName("Basic arithmetic test")
    void testBasicArithmetic() {
        int sum = 2 + 3;
        assertEquals(5, sum, "2 + 3 should equal 5");
    }

    @Test
    @DisplayName("String concatenation test")
    void testStringConcatenation() {
        String hello = "Hello";
        String world = "World";
        String result = hello + " " + world;
        assertEquals("Hello World", result, "Should concatenate strings correctly");
    }
}

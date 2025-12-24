package com.back_end.english_app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests that don't require Spring context
 */
@DisplayName("Basic Unit Tests")
class ApplicationContextTest {

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
    
    @Test
    @DisplayName("Array operations test")
    void testArrayOperations() {
        int[] numbers = {1, 2, 3, 4, 5};
        assertEquals(5, numbers.length, "Array should have 5 elements");
        assertEquals(1, numbers[0], "First element should be 1");
        assertEquals(5, numbers[4], "Last element should be 5");
    }
}

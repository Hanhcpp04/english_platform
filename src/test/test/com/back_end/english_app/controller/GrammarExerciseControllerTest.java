package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.controller.grammar.GrammarExerciseController;
import com.back_end.english_app.dto.respones.grammar.GrammarExerciseResponseDTO;
import com.back_end.english_app.dto.respones.grammar.ExerciseTypeStructureDTO;
import com.back_end.english_app.service.user.GrammarExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Grammar Exercise Controller Tests")
class GrammarExerciseControllerTest {

    @Mock
    private GrammarExerciseService grammarExerciseService;

    @InjectMocks
    private GrammarExerciseController grammarExerciseController;

    private GrammarExerciseResponseDTO mockExerciseResponse;
    private ExerciseTypeStructureDTO mockExerciseType;

    @BeforeEach
    void setUp() {
        // Setup mock data
        mockExerciseResponse = new GrammarExerciseResponseDTO();
        mockExerciseResponse.setTopicId(1L);
        mockExerciseResponse.setLessonId(1L);
        mockExerciseResponse.setTypeId(1L);

        mockExerciseType = new ExerciseTypeStructureDTO();
        mockExerciseType.setTypeId(1L);
        mockExerciseType.setTypeName("Multiple Choice");
    }

    @Test
    @DisplayName("Should get exercise types successfully")
    void testGetExerciseTypes_Success() {
        // Given
        Long topicId = 1L;
        Long lessonId = 1L;
        List<ExerciseTypeStructureDTO> mockTypes = Arrays.asList(mockExerciseType);

        when(grammarExerciseService.getExerciseTypes(topicId, lessonId)).thenReturn(mockTypes);

        // When
        APIResponse<List<ExerciseTypeStructureDTO>> response = 
                grammarExerciseController.getExerciseTypes(topicId, lessonId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(1, response.getResult().size());
        assertEquals("Multiple Choice", response.getResult().get(0).getTypeName());
        verify(grammarExerciseService, times(1)).getExerciseTypes(topicId, lessonId);
    }

    @Test
    @DisplayName("Should get questions successfully")
    void testGetQuestions_Success() {
        // Given
        Long topicId = 1L;
        Long lessonId = 1L;
        Long typeId = 1L;

        when(grammarExerciseService.getQuestions(topicId, lessonId, typeId))
                .thenReturn(mockExerciseResponse);

        // When
        APIResponse<GrammarExerciseResponseDTO> response = 
                grammarExerciseController.getQuestions(topicId, lessonId, typeId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(topicId, response.getResult().getTopicId());
        assertEquals(lessonId, response.getResult().getLessonId());
        verify(grammarExerciseService, times(1)).getQuestions(topicId, lessonId, typeId);
    }

    @Test
    @DisplayName("Should handle error when getting questions")
    void testGetQuestions_Error() {
        // Given
        Long topicId = 1L;
        Long lessonId = 1L;
        Long typeId = 1L;

        when(grammarExerciseService.getQuestions(topicId, lessonId, typeId))
                .thenThrow(new RuntimeException("Database error"));

        // When
        APIResponse<GrammarExerciseResponseDTO> response = 
                grammarExerciseController.getQuestions(topicId, lessonId, typeId);

        // Then
        assertNotNull(response);
        assertNotEquals(1000, response.getCode());
        verify(grammarExerciseService, times(1)).getQuestions(topicId, lessonId, typeId);
    }
}

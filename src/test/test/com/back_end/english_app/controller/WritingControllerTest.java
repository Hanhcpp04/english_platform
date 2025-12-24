package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.controller.writting.WritingController;
import com.back_end.english_app.dto.request.writing.SubmitWritingRequest;
import com.back_end.english_app.dto.respones.writing.GradingResultResponse;
import com.back_end.english_app.dto.respones.writing.WritingTopicResponse;
import com.back_end.english_app.dto.respones.writing.WritingTaskResponse;
import com.back_end.english_app.service.user.WritingService;
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
@DisplayName("Writing Controller Tests")
class WritingControllerTest {

    @Mock
    private WritingService writingService;

    @InjectMocks
    private WritingController writingController;

    private WritingTopicResponse mockTopicResponse;
    private WritingTaskResponse mockTaskResponse;
    private GradingResultResponse mockGradingResult;

    @BeforeEach
    void setUp() {
        // Setup mock topic
        mockTopicResponse = new WritingTopicResponse();
        mockTopicResponse.setTopicId(1);
        mockTopicResponse.setTopicName("Academic Writing");

        // Setup mock task
        mockTaskResponse = new WritingTaskResponse();
        mockTaskResponse.setTaskId(1);
        mockTaskResponse.setTaskTitle("Essay Writing");

        // Setup mock grading result
        mockGradingResult = new GradingResultResponse();
        mockGradingResult.setSubmissionId(1L);
        mockGradingResult.setOverallScore(7.5);
    }

    @Test
    @DisplayName("Should get all writing topics successfully")
    void testGetAllTopics_Success() {
        // Given
        List<WritingTopicResponse> mockTopics = Arrays.asList(mockTopicResponse);
        when(writingService.getAllTopics()).thenReturn(mockTopics);

        // When
        APIResponse<List<WritingTopicResponse>> response = writingController.getAllTopics();

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(1, response.getResult().size());
        assertEquals("Academic Writing", response.getResult().get(0).getTopicName());
        verify(writingService, times(1)).getAllTopics();
    }

    @Test
    @DisplayName("Should get tasks by topic successfully")
    void testGetTasksByTopic_Success() {
        // Given
        Integer topicId = 1;
        List<WritingTaskResponse> mockTasks = Arrays.asList(mockTaskResponse);
        when(writingService.getTasksByTopicId(topicId)).thenReturn(mockTasks);

        // When
        APIResponse<List<WritingTaskResponse>> response = writingController.getTasksByTopic(topicId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(1, response.getResult().size());
        assertEquals("Essay Writing", response.getResult().get(0).getTaskTitle());
        verify(writingService, times(1)).getTasksByTopicId(topicId);
    }

    @Test
    @DisplayName("Should submit writing and get grading result successfully")
    void testSubmitWriting_Success() {
        // Given
        Long userId = 1L;
        SubmitWritingRequest request = new SubmitWritingRequest();
        request.setPromptId(1);
        request.setUserAnswer("This is my essay...");

        when(writingService.submitWriting(any(SubmitWritingRequest.class), eq(userId)))
                .thenReturn(mockGradingResult);

        // When
        APIResponse<GradingResultResponse> response = writingController.submitWriting(request, userId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(7.5, response.getResult().getOverallScore());
        verify(writingService, times(1)).submitWriting(any(SubmitWritingRequest.class), eq(userId));
    }

    @Test
    @DisplayName("Should handle error when fetching topics")
    void testGetAllTopics_Error() {
        // Given
        when(writingService.getAllTopics()).thenThrow(new RuntimeException("Database error"));

        // When
        APIResponse<List<WritingTopicResponse>> response = writingController.getAllTopics();

        // Then
        assertNotNull(response);
        assertNotEquals(1000, response.getCode());
        verify(writingService, times(1)).getAllTopics();
    }
}

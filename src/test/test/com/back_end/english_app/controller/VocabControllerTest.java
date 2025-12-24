package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.controller.vocab.VocabController;
import com.back_end.english_app.dto.request.vocab.CompleteWordRequest;
import com.back_end.english_app.dto.respones.vocab.VocabWordResponse;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.service.user.VocabWordService;
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
@DisplayName("Vocab Controller Tests")
class VocabControllerTest {

    @Mock
    private VocabWordService vocabWordService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VocabController vocabController;

    private VocabWordResponse mockVocabWordResponse;
    private CompleteWordRequest completeWordRequest;

    @BeforeEach
    void setUp() {
        // Setup mock data
        mockVocabWordResponse = new VocabWordResponse();
        mockVocabWordResponse.setWordId(1L);
        mockVocabWordResponse.setWord("Hello");
        mockVocabWordResponse.setMeaning("Xin chào");
        mockVocabWordResponse.setCompleted(true);

        completeWordRequest = new CompleteWordRequest();
        completeWordRequest.setWordId(1L);
    }

    @Test
    @DisplayName("Should get words by topic successfully")
    void testGetWordsByTopic_Success() {
        // Given
        Long topicId = 1L;
        Long userId = 1L;
        List<VocabWordResponse> mockWords = Arrays.asList(mockVocabWordResponse);

        when(vocabWordService.getWordsByTopic(topicId, userId)).thenReturn(mockWords);

        // When
        APIResponse<List<VocabWordResponse>> response = vocabController.getWordsByTopic(topicId, userId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(1, response.getResult().size());
        assertEquals("Hello", response.getResult().get(0).getWord());
        verify(vocabWordService, times(1)).getWordsByTopic(topicId, userId);
    }

    @Test
    @DisplayName("Should complete word successfully")
    void testCompleteWord_Success() {
        // Given
        Long userId = 1L;
        when(vocabWordService.completeWord(any(CompleteWordRequest.class), eq(userId)))
                .thenReturn(mockVocabWordResponse);

        // When
        APIResponse<VocabWordResponse> response = vocabController.completeWord(completeWordRequest, userId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertTrue(response.getResult().isCompleted());
        verify(vocabWordService, times(1)).completeWord(any(CompleteWordRequest.class), eq(userId));
    }

    @Test
    @DisplayName("Should return empty list when no words found for topic")
    void testGetWordsByTopic_EmptyList() {
        // Given
        Long topicId = 999L;
        Long userId = 1L;
        when(vocabWordService.getWordsByTopic(topicId, userId)).thenReturn(Arrays.asList());

        // When
        APIResponse<List<VocabWordResponse>> response = vocabController.getWordsByTopic(topicId, userId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertTrue(response.getResult().isEmpty());
        verify(vocabWordService, times(1)).getWordsByTopic(topicId, userId);
    }
}

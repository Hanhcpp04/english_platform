package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.controller.dashboard.DashboardController;
import com.back_end.english_app.dto.respones.dashboard.DashboardSummaryDTO;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.service.user.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Dashboard Controller Tests")
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardController dashboardController;

    private UserEntity mockUser;
    private DashboardSummaryDTO mockDashboardSummary;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = new UserEntity();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@example.com");

        // Setup mock dashboard summary
        mockDashboardSummary = new DashboardSummaryDTO();
        mockDashboardSummary.setTotalVocabulary(100);
        mockDashboardSummary.setCompletedVocabulary(50);
        mockDashboardSummary.setTotalGrammarLessons(20);
        mockDashboardSummary.setCompletedGrammarLessons(10);

        // Setup authentication mock
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
    }

    @Test
    @DisplayName("Should get dashboard summary for authenticated user successfully")
    void testGetDashboardSummary_Success() {
        // Given
        when(dashboardService.getDashboardSummary(1L)).thenReturn(mockDashboardSummary);

        // When
        APIResponse<DashboardSummaryDTO> response = dashboardController.getDashboardSummary(authentication);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(100, response.getResult().getTotalVocabulary());
        assertEquals(50, response.getResult().getCompletedVocabulary());
        verify(dashboardService, times(1)).getDashboardSummary(1L);
    }

    @Test
    @DisplayName("Should get dashboard summary by userId successfully")
    void testGetDashboardSummaryByUserId_Success() {
        // Given
        Long userId = 1L;
        when(dashboardService.getDashboardSummary(userId)).thenReturn(mockDashboardSummary);

        // When
        APIResponse<DashboardSummaryDTO> response = dashboardController.getDashboardSummaryByUserId(userId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(20, response.getResult().getTotalGrammarLessons());
        assertEquals(10, response.getResult().getCompletedGrammarLessons());
        verify(dashboardService, times(1)).getDashboardSummary(userId);
    }

    @Test
    @DisplayName("Should verify dashboard summary data integrity")
    void testDashboardSummary_DataIntegrity() {
        // Given
        when(dashboardService.getDashboardSummary(1L)).thenReturn(mockDashboardSummary);

        // When
        APIResponse<DashboardSummaryDTO> response = dashboardController.getDashboardSummary(authentication);

        // Then
        assertNotNull(response.getResult());
        assertTrue(response.getResult().getTotalVocabulary() >= response.getResult().getCompletedVocabulary());
        assertTrue(response.getResult().getTotalGrammarLessons() >= response.getResult().getCompletedGrammarLessons());
    }
}

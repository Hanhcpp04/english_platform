package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.controller.badge.BadgeController;
import com.back_end.english_app.dto.respones.badge.UserBadgesSummaryDTO;
import com.back_end.english_app.entity.UserEntity;
import com.back_end.english_app.repository.UserRepository;
import com.back_end.english_app.service.user.BadgeCheckService;
import com.back_end.english_app.service.user.BadgeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Badge Controller Tests")
class BadgeControllerTest {

    @Mock
    private BadgeService badgeService;

    @Mock
    private BadgeCheckService badgeCheckService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BadgeController badgeController;

    private UserEntity mockUser;
    private UserBadgesSummaryDTO mockBadgesSummary;

    @BeforeEach
    void setUp() {
        // Setup mock user
        mockUser = new UserEntity();
        mockUser.setUserId(1L);
        mockUser.setEmail("test@example.com");

        // Setup mock badges summary
        mockBadgesSummary = new UserBadgesSummaryDTO();
        mockBadgesSummary.setTotalBadges(10);
        mockBadgesSummary.setEarnedBadges(5);

        // Setup authentication mock
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
    }

    @Test
    @DisplayName("Should get earned badges successfully")
    void testGetEarnedBadges_Success() {
        // Given
        when(badgeService.getAllUserBadges(1L)).thenReturn(mockBadgesSummary);

        // When
        APIResponse<UserBadgesSummaryDTO> response = badgeController.getEarnedBadges(authentication);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(10, response.getResult().getTotalBadges());
        assertEquals(5, response.getResult().getEarnedBadges());
        verify(badgeService, times(1)).getAllUserBadges(1L);
    }

    @Test
    @DisplayName("Should force check all badges successfully")
    void testForceCheckBadges_Success() {
        // Given
        doNothing().when(badgeCheckService).checkAllBadges(1L);

        // When
        APIResponse<Map<String, Object>> response = badgeController.forceCheckBadges(authentication);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertTrue((Boolean) response.getResult().get("success"));
        assertEquals("All badges checked successfully", response.getResult().get("message"));
        verify(badgeCheckService, times(1)).checkAllBadges(1L);
    }

    @Test
    @DisplayName("Should return correct user badge summary structure")
    void testGetEarnedBadges_CorrectStructure() {
        // Given
        when(badgeService.getAllUserBadges(1L)).thenReturn(mockBadgesSummary);

        // When
        APIResponse<UserBadgesSummaryDTO> response = badgeController.getEarnedBadges(authentication);

        // Then
        assertNotNull(response);
        assertNotNull(response.getResult());
        assertTrue(response.getResult().getTotalBadges() >= response.getResult().getEarnedBadges());
    }
}

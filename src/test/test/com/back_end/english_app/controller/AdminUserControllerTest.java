package com.back_end.english_app.controller;

import com.back_end.english_app.config.APIResponse;
import com.back_end.english_app.controller.user.AdminUserController;
import com.back_end.english_app.dto.respones.user.AdminUserResponse;
import com.back_end.english_app.entity.Role;
import com.back_end.english_app.service.admin.AdminUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin User Controller Tests")
class AdminUserControllerTest {

    @Mock
    private AdminUserService adminUserService;

    @InjectMocks
    private AdminUserController adminUserController;

    private AdminUserResponse mockUserResponse;

    @BeforeEach
    void setUp() {
        // Setup mock user response
        mockUserResponse = new AdminUserResponse();
        mockUserResponse.setUserId(1L);
        mockUserResponse.setEmail("user@example.com");
        mockUserResponse.setFullName("Test User");
        mockUserResponse.setRole(Role.USER);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void testGetAllUsers_Success() {
        // Given
        int page = 0;
        int size = 10;
        List<AdminUserResponse> userList = Arrays.asList(mockUserResponse);
        Page<AdminUserResponse> mockPage = new PageImpl<>(userList, PageRequest.of(page, size), userList.size());
        
        when(adminUserService.getAllUsers(page, size)).thenReturn(mockPage);

        // When
        APIResponse<Page<AdminUserResponse>> response = adminUserController.getAllUsers(page, size);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(1, response.getResult().getTotalElements());
        assertEquals("user@example.com", response.getResult().getContent().get(0).getEmail());
        verify(adminUserService, times(1)).getAllUsers(page, size);
    }

    @Test
    @DisplayName("Should get user profile by userId successfully")
    void testGetUserProfile_Success() {
        // Given
        Long userId = 1L;
        when(adminUserService.getUserProfile(userId)).thenReturn(mockUserResponse);

        // When
        APIResponse<AdminUserResponse> response = adminUserController.getUserProfile(userId);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(userId, response.getResult().getUserId());
        assertEquals("Test User", response.getResult().getFullName());
        verify(adminUserService, times(1)).getUserProfile(userId);
    }

    @Test
    @DisplayName("Should update user role successfully")
    void testUpdateUserRole_Success() {
        // Given
        Long userId = 1L;
        Role newRole = Role.ADMIN;
        mockUserResponse.setRole(newRole);
        
        when(adminUserService.updateUserRole(userId, newRole)).thenReturn(mockUserResponse);

        // When
        APIResponse<AdminUserResponse> response = adminUserController.updateUserRole(userId, newRole);

        // Then
        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertNotNull(response.getResult());
        assertEquals(Role.ADMIN, response.getResult().getRole());
        verify(adminUserService, times(1)).updateUserRole(userId, newRole);
    }

    @Test
    @DisplayName("Should handle pagination correctly")
    void testGetAllUsers_Pagination() {
        // Given
        int page = 0;
        int size = 5;
        List<AdminUserResponse> userList = Arrays.asList(mockUserResponse);
        Page<AdminUserResponse> mockPage = new PageImpl<>(userList, PageRequest.of(page, size), userList.size());
        
        when(adminUserService.getAllUsers(page, size)).thenReturn(mockPage);

        // When
        APIResponse<Page<AdminUserResponse>> response = adminUserController.getAllUsers(page, size);

        // Then
        assertNotNull(response.getResult());
        assertEquals(page, response.getResult().getNumber());
        assertEquals(size, response.getResult().getSize());
        assertTrue(response.getResult().getContent().size() <= size);
    }
}

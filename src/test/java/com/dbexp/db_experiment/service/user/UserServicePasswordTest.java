package com.dbexp.db_experiment.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.ChangePasswordRequest;
import com.dbexp.db_experiment.dto.user.ChangePasswordResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service - Password Change Tests")
class UserServicePasswordTest extends BaseUserServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Password Changes")
    class SuccessTests {

        @Test
        @DisplayName("Should change password successfully with valid data")
        void changePassword_Success() {
            // Arrange
            Long userId = 1L;
            String currentPassword = "oldPassword123";
            String newPassword = "newPassword456";
            String hashedCurrentPassword = "hashedOldPassword";
            String hashedNewPassword = "hashedNewPassword";
            ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

            User existingUser = createMockUser(userId, "testuser", "test@example.com", hashedCurrentPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            when(passwordEncoder.matches(currentPassword, hashedCurrentPassword)).thenReturn(true);
            when(passwordEncoder.matches(newPassword, hashedCurrentPassword)).thenReturn(false);
            mockPasswordEncoderEncode(newPassword, hashedNewPassword);
            when(userRepository.updatePassword(userId, hashedNewPassword)).thenReturn(1);

            // Act
            ChangePasswordResponse response = userService.changePassword(session, userId, request);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.getUserId());
            assertNotNull(response.getUpdatedAt());
            assertEquals("Password changed successfully", response.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedCurrentPassword);
            verify(passwordEncoder).matches(newPassword, hashedCurrentPassword);
            verify(passwordEncoder).encode(newPassword);
            verify(userRepository).updatePassword(userId, hashedNewPassword);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should throw exception when user not found")
        void changePassword_UserNotFound_ThrowsException() {
            // Arrange
            Long userId = 999L;
            ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "newPassword");

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindByIdNotFound(userId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userService.changePassword(session, userId, request);
            });

            assertEquals("User not found", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder, never()).matches(any(), any());
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).updatePassword(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when current password is incorrect")
        void changePassword_IncorrectCurrentPassword_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String currentPassword = "wrongPassword";
            String newPassword = "newPassword456";
            String hashedCurrentPassword = "hashedOldPassword";
            ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

            User existingUser = createMockUser(userId, "testuser", "test@example.com", hashedCurrentPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(currentPassword, hashedCurrentPassword, false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.changePassword(session, userId, request);
            });

            assertEquals("Current password is incorrect", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedCurrentPassword);
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).updatePassword(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when new password is same as current")
        void changePassword_SamePassword_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String currentPassword = "samePassword123";
            String newPassword = "samePassword123";
            String hashedPassword = "hashedPassword";
            ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

            User existingUser = createMockUser(userId, "testuser", "test@example.com", hashedPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(currentPassword, hashedPassword, true);
            when(passwordEncoder.matches(newPassword, hashedPassword)).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.changePassword(session, userId, request);
            });

            assertEquals("New password must be different from current password", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder, times(2)).matches(currentPassword, hashedPassword);
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).updatePassword(any(), any());
        }
    }

    @Nested
    @DisplayName("Database Operation Failures")
    class DatabaseFailureTests {

        @Test
        @DisplayName("Should throw exception when password update fails")
        void changePassword_UpdateFailed_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String currentPassword = "oldPassword123";
            String newPassword = "newPassword456";
            String hashedCurrentPassword = "hashedOldPassword";
            String hashedNewPassword = "hashedNewPassword";
            ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);

            User existingUser = createMockUser(userId, "testuser", "test@example.com", hashedCurrentPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            when(passwordEncoder.matches(currentPassword, hashedCurrentPassword)).thenReturn(true);
            when(passwordEncoder.matches(newPassword, hashedCurrentPassword)).thenReturn(false);
            mockPasswordEncoderEncode(newPassword, hashedNewPassword);
            when(userRepository.updatePassword(userId, hashedNewPassword)).thenReturn(0);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                userService.changePassword(session, userId, request);
            });

            assertEquals("Failed to update password", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedCurrentPassword);
            verify(passwordEncoder).matches(newPassword, hashedCurrentPassword);
            verify(passwordEncoder).encode(newPassword);
            verify(userRepository).updatePassword(userId, hashedNewPassword);
        }
    }
}
package com.dbexp.db_experiment.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.DeleteAccountRequest;
import com.dbexp.db_experiment.dto.user.DeleteAccountResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service - Account Deletion Tests")
class UserServiceDeleteTest extends BaseUserServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Account Deletions")
    class SuccessTests {

        @Test
        @DisplayName("Should delete account successfully with valid password")
        void deleteAccount_Success() {
            // Arrange
            Long userId = 1L;
            String password = "currentPassword123";
            String hashedPassword = "hashedPassword";
            DeleteAccountRequest request = new DeleteAccountRequest(password);

            User existingUser = createMockUser(userId, "testuser", "test@example.com", hashedPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(password, hashedPassword, true);
            when(userRepository.deleteByUserId(userId)).thenReturn(1);

            // Act
            DeleteAccountResponse response = userService.deleteAccount(session, userId, request);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.getUserId());
            assertNotNull(response.getDeletedAt());
            assertEquals("Account deleted successfully", response.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(password, hashedPassword);
            verify(userRepository).deleteByUserId(userId);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should throw exception when user not found")
        void deleteAccount_UserNotFound_ThrowsException() {
            // Arrange
            Long userId = 999L;
            DeleteAccountRequest request = new DeleteAccountRequest("password123");

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindByIdNotFound(userId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userService.deleteAccount(session, userId, request);
            });

            assertEquals("User not found", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder, never()).matches(any(), any());
            verify(userRepository, never()).deleteByUserId(any());
        }

        @Test
        @DisplayName("Should throw exception when password is incorrect")
        void deleteAccount_IncorrectPassword_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String password = "wrongPassword";
            String hashedPassword = "hashedPassword";
            DeleteAccountRequest request = new DeleteAccountRequest(password);

            mockAuthenticatedUser(userId);
            User existingUser = createMockUser(userId, "testuser", "test@example.com", hashedPassword);

            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(password, hashedPassword, false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.deleteAccount(session, userId, request);
            });

            assertEquals("Current password is incorrect", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(password, hashedPassword);
            verify(userRepository, never()).deleteByUserId(any());
        }
    }

    @Nested
    @DisplayName("Database Operation Failures")
    class DatabaseFailureTests {

        @Test
        @DisplayName("Should throw exception when account deletion fails")
        void deleteAccount_DeleteFailed_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String password = "currentPassword123";
            String hashedPassword = "hashedPassword";
            DeleteAccountRequest request = new DeleteAccountRequest(password);

            mockAuthenticatedUser(userId);
            User existingUser = createMockUser(userId, "testuser", "test@example.com", hashedPassword);

            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(password, hashedPassword, true);
            when(userRepository.deleteByUserId(userId)).thenReturn(0);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                userService.deleteAccount(session, userId, request);
            });

            assertEquals("Failed to delete account", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(password, hashedPassword);
            verify(userRepository).deleteByUserId(userId);
        }
    }
}
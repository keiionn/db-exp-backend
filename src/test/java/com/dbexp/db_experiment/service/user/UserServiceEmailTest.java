package com.dbexp.db_experiment.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.ChangeEmailRequest;
import com.dbexp.db_experiment.dto.user.ChangeEmailResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.exception.ConflictException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service - Email Change Tests")
class UserServiceEmailTest extends BaseUserServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Email Changes")
    class SuccessTests {

        @Test
        @DisplayName("Should change email successfully with valid data")
        void changeEmail_Success() {
            // Arrange
            Long userId = 1L;
            String oldEmail = "old@example.com";
            String newEmail = "new@example.com";
            String currentPassword = "currentPassword123";
            String hashedPassword = "hashedPassword";
            ChangeEmailRequest request = new ChangeEmailRequest(currentPassword, newEmail);

            mockAuthenticatedUser(userId);
            User existingUser = createMockUser(userId, "testuser", oldEmail, hashedPassword);

            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(currentPassword, hashedPassword, true);
            mockEmailExists(newEmail, false);
            when(userRepository.updateEmail(userId, newEmail)).thenReturn(1);

            // Act
            ChangeEmailResponse response = userService.changeEmail(session, userId, request);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.getUserId());
            assertEquals(oldEmail, response.getOldEmail());
            assertEquals(newEmail, response.getNewEmail());
            assertNotNull(response.getUpdatedAt());
            assertEquals("Email changed successfully", response.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedPassword);
            verify(userRepository).existsByEmail(newEmail);
            verify(userRepository).updateEmail(userId, newEmail);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should throw exception when user not found")
        void changeEmail_UserNotFound_ThrowsException() {
            // Arrange
            Long userId = 999L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "new@example.com");

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindByIdNotFound(userId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userService.changeEmail(session, userId, request);
            });

            assertEquals("User not found", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder, never()).matches(any(), any());
            verify(userRepository, never()).existsByEmail(any());
            verify(userRepository, never()).updateEmail(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when current password is incorrect")
        void changeEmail_IncorrectCurrentPassword_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String currentPassword = "wrongPassword";
            String newEmail = "new@example.com";
            String hashedPassword = "hashedPassword";
            ChangeEmailRequest request = new ChangeEmailRequest(currentPassword, newEmail);

            User existingUser = createMockUser(userId, "testuser", "old@example.com", hashedPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(currentPassword, hashedPassword, false);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.changeEmail(session, userId, request);
            });

            assertEquals("Current password is incorrect", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedPassword);
            verify(userRepository, never()).existsByEmail(any());
            verify(userRepository, never()).updateEmail(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when new email is same as current")
        void changeEmail_SameEmail_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String currentEmail = "current@example.com";
            String newEmail = "current@example.com";
            String currentPassword = "currentPassword123";
            String hashedPassword = "hashedPassword";
            ChangeEmailRequest request = new ChangeEmailRequest(currentPassword, newEmail);

            User existingUser = createMockUser(userId, "testuser", currentEmail, hashedPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(currentPassword, hashedPassword, true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.changeEmail(session, userId, request);
            });

            assertEquals("New email must be different from current email", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedPassword);
            verify(userRepository, never()).existsByEmail(any());
            verify(userRepository, never()).updateEmail(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void changeEmail_EmailAlreadyExists_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String currentEmail = "current@example.com";
            String newEmail = "existing@example.com";
            String currentPassword = "currentPassword123";
            String hashedPassword = "hashedPassword";
            ChangeEmailRequest request = new ChangeEmailRequest(currentPassword, newEmail);

            User existingUser = createMockUser(userId, "testuser", currentEmail, hashedPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(currentPassword, hashedPassword, true);
            mockEmailExists(newEmail, true);

            // Act & Assert
            ConflictException exception = assertThrows(ConflictException.class, () -> {
                userService.changeEmail(session, userId, request);
            });

            assertEquals("Email already exists", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedPassword);
            verify(userRepository).existsByEmail(newEmail);
            verify(userRepository, never()).updateEmail(any(), any());
        }
    }

    @Nested
    @DisplayName("Database Operation Failures")
    class DatabaseFailureTests {

        @Test
        @DisplayName("Should throw exception when email update fails")
        void changeEmail_UpdateFailed_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String oldEmail = "old@example.com";
            String newEmail = "new@example.com";
            String currentPassword = "currentPassword123";
            String hashedPassword = "hashedPassword";
            ChangeEmailRequest request = new ChangeEmailRequest(currentPassword, newEmail);

            User existingUser = createMockUser(userId, "testuser", oldEmail, hashedPassword);

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockPasswordEncoderMatches(currentPassword, hashedPassword, true);
            mockEmailExists(newEmail, false);
            when(userRepository.updateEmail(userId, newEmail)).thenReturn(0);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.changeEmail(session, userId, request);
            });

            assertEquals("Failed to update email", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(passwordEncoder).matches(currentPassword, hashedPassword);
            verify(userRepository).existsByEmail(newEmail);
            verify(userRepository).updateEmail(userId, newEmail);
        }
    }
}
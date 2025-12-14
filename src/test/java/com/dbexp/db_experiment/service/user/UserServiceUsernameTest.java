package com.dbexp.db_experiment.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.ChangeUsernameRequest;
import com.dbexp.db_experiment.dto.user.ChangeUsernameResponse;
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

/**
 * Test class for UserService username change operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service - Username Change Tests")
class UserServiceUsernameTest extends BaseUserServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Username Changes")
    class SuccessTests {

        @Test
        @DisplayName("Should change username successfully with valid data")
        void changeUsername_Success() {
            // Arrange
            Long userId = 1L;
            String oldUsername = "olduser";
            String newUsername = "newuser";
            ChangeUsernameRequest request = new ChangeUsernameRequest(newUsername);

            User existingUser = createMockUser(userId, oldUsername, "test@example.com", "hashedPassword");

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockUsernameExists(newUsername, false);
            when(userRepository.updateUsername(userId, newUsername)).thenReturn(1);

            // Act
            ChangeUsernameResponse response = userService.changeUsername(session, userId, request);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.getUserId());
            assertEquals(oldUsername, response.getOldUsername());
            assertEquals(newUsername, response.getNewUsername());
            assertNotNull(response.getUpdatedAt());

            verify(userRepository).findById(userId);
            verify(userRepository).existsByUsername(newUsername);
            verify(userRepository).updateUsername(userId, newUsername);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should throw exception when user not found")
        void changeUsername_UserNotFound_ThrowsException() {
            // Arrange
            Long userId = 999L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("newuser");

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindByIdNotFound(userId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userService.changeUsername(session, userId, request);
            });

            assertEquals("User not found", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(userRepository, never()).existsByUsername(any());
            verify(userRepository, never()).updateUsername(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void changeUsername_UsernameAlreadyExists_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String oldUsername = "olduser";
            String newUsername = "existinguser";
            ChangeUsernameRequest request = new ChangeUsernameRequest(newUsername);

            User existingUser = createMockUser(userId, oldUsername, "test@example.com", "hashedPassword");

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);
            mockUsernameExists(newUsername, true);

            // Act & Assert
            ConflictException exception = assertThrows(ConflictException.class, () -> {
                userService.changeUsername(session, userId, request);
            });

            assertEquals("Username already exists", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(userRepository).existsByUsername(newUsername);
            verify(userRepository, never()).updateUsername(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when new username is same as current")
        void changeUsername_SameUsername_ThrowsException() {
            // Arrange
            Long userId = 1L;
            String currentUsername = "testuser";
            ChangeUsernameRequest request = new ChangeUsernameRequest(currentUsername);

            User existingUser = createMockUser(userId, currentUsername, "test@example.com", "hashedPassword");

            mockAuthenticatedUser(userId);
            mockUserRepositoryFindById(userId, existingUser);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.changeUsername(session, userId, request);
            });

            assertEquals("New username must be different from current username", exception.getMessage());

            verify(userRepository).findById(userId);
            verify(userRepository, never()).existsByUsername(any());
            verify(userRepository, never()).updateUsername(any(), any());
        }
    }
}
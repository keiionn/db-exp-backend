package com.dbexp.db_experiment.service.user;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.GetUserByUsernameRequest;
import com.dbexp.db_experiment.dto.user.GetUserByUsernameResponse;
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
@DisplayName("User Service - Get By Username Tests")
public class UserServiceGetByUsernameTest extends BaseUserServiceTest {
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    // Helper method to mock userRepository.findByUsername()
    private void mockUserRepositoryFindByUsername(String username, User user) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
    }

    // Helper method to mock user not found scenario
    private void mockUserRepositoryFindByUsernameNotFound(String username) {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
    }

    @Nested
    @DisplayName("Successful Get By Username Operations")
    class SuccessTests {
        @Test
        @DisplayName("Should retrieve user successfully by username")
        void getByUsername_Success() {
            // Arrange
            String username = "testuser";
            GetUserByUsernameRequest request = new GetUserByUsernameRequest(username);

            User mockUser = createMockUser(1L, username, "test@example.com", "hashedPassword");
            mockUserRepositoryFindByUsername(username, mockUser);

            // Act
            GetUserByUsernameResponse response = userService.getUserByUsername(request);

            // Assert
            assertNotNull(response);
            assertEquals(mockUser.getUserId(), response.getUserId());
            assertEquals(mockUser.getUsername(), response.getUsername());
            assertEquals(mockUser.getEmail(), response.getEmail());
            assertNotNull(response.getCreationTime());

            verify(userRepository).findByUsername(username);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {
        @Test
        @DisplayName("Should throw exception when username is null")
        void getUserByUsername_NullUsername_ThrowsException() {
            // Arrange
            GetUserByUsernameRequest request = new GetUserByUsernameRequest(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.getUserByUsername(request);
            });

            assertEquals("Username is required", exception.getMessage());
            verify(userRepository, never()).findByUsername(any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void getUserByUsername_UserNotFound_ThrowsException() {
            // Arrange
            String username = "nonExistingUser";
            GetUserByUsernameRequest request = new GetUserByUsernameRequest(username);

            mockUserRepositoryFindByUsernameNotFound(username);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userService.getUserByUsername(request);
            });

            assertEquals("User not found", exception.getMessage());
            verify(userRepository).findByUsername(username);
        }
    }
}
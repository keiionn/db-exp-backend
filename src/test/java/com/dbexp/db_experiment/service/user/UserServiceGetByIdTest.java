package com.dbexp.db_experiment.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.GetUserByIdRequest;
import com.dbexp.db_experiment.dto.user.GetUserByIdResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service - Get By ID Tests")
public class UserServiceGetByIdTest extends BaseUserServiceTest {
    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Get By ID Operations")
    class SuccessTests {
        @Test
        @DisplayName("Should retrieve user successfully by ID")
        void getById_Success() {
            // Arrange
            Long userId = 1L;
            GetUserByIdRequest request = new GetUserByIdRequest(userId);

            User mockUser = createMockUser(userId, "testuser", "test@example.com", "hashedPassword");
            mockUserRepositoryFindById(userId, mockUser);

            // Act
            GetUserByIdResponse response = userService.getUserById(request);

            // Assert
            assertNotNull(response);
            assertEquals(mockUser.getUserId(), response.getUserId());
            assertEquals(mockUser.getUsername(), response.getUsername());
            assertEquals(mockUser.getEmail(), response.getEmail());
            assertNotNull(response.getCreationTime());

            verify(userRepository).findById(userId);
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicErrorTests {
        @Test
        @DisplayName("Should throw exception when user ID is null")
        void getUserById_NullUserId_ThrowsException() {
            // Arrange
            GetUserByIdRequest request = new GetUserByIdRequest(null);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                userService.getUserById(request);
            });

            assertEquals("User ID is required", exception.getMessage());
            verify(userRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void getUserById_UserNotFound_ThrowsException() {
            // Arrange
            Long userId = 999L;
            GetUserByIdRequest request = new GetUserByIdRequest(userId);

            mockUserRepositoryFindByIdNotFound(userId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                userService.getUserById(request);
            });

            assertEquals("User not found", exception.getMessage());
            verify(userRepository).findById(userId);
        }
    }
}

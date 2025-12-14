package com.dbexp.db_experiment.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.CreateUserRequest;
import com.dbexp.db_experiment.dto.user.CreateUserResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.exception.ConflictException;
import com.dbexp.db_experiment.testutil.UserTestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service - Create User Tests")
class UserServiceCreateTest extends BaseUserServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Creation")
    class SuccessTests {

        @Test
        @DisplayName("Should create user successfully with valid data")
        void createUser_Success() {
            // Arrange
            CreateUserRequest request = createValidRequest();
            String hashedPassword = "hashedPassword123";
            User savedUser = UserTestBuilder.aUser()
                    .withUserId(1L)
                    .withUsername(request.getUsername())
                    .withEmail(request.getEmail())
                    .withPassword(hashedPassword)
                    .build();

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn(hashedPassword);
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // Act
            CreateUserResponse response = userService.createUser(request);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.getUserId());
            assertEquals(request.getUsername(), response.getUsername());
            assertEquals(request.getEmail(), response.getEmail());
            assertNotNull(response.getCreatedAt());

            verify(userRepository).existsByUsername(request.getUsername());
            verify(userRepository).existsByEmail(request.getEmail());
            verify(passwordEncoder).encode(request.getPassword());
            verify(userRepository).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Duplicate Checks")
    class DuplicateTests {

        @Test
        @DisplayName("Should throw exception when username already exists")
        void createUser_DuplicateUsername_ThrowsException() {
            // Arrange
            CreateUserRequest request = createValidRequest();

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

            // Act & Assert
            ConflictException exception = assertThrows(ConflictException.class, () -> {
                userService.createUser(request);
            });

            assertEquals("Username already exists", exception.getMessage());

            verify(userRepository).existsByUsername(request.getUsername());
            verify(userRepository, never()).existsByEmail(any());
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void createUser_DuplicateEmail_ThrowsException() {
            // Arrange
            CreateUserRequest request = createValidRequest();

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            // Act & Assert
            ConflictException exception = assertThrows(ConflictException.class, () -> {
                userService.createUser(request);
            });

            assertEquals("Email already exists", exception.getMessage());

            verify(userRepository).existsByUsername(request.getUsername());
            verify(userRepository).existsByEmail(request.getEmail());
            verify(passwordEncoder, never()).encode(any());
            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when username is null")
        void createUser_NullUsername_ThrowsException() {
            // Arrange
            CreateUserRequest request = createRequest(null, "test@example.com", "password123");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                userService.createUser(request);
            });

            verifyNoInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void createUser_NullEmail_ThrowsException() {
            // Arrange
            CreateUserRequest request = createRequest("testuser", null, "password123");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                userService.createUser(request);
            });

            verifyNoInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }

        @Test
        @DisplayName("Should throw exception when password is null")
        void createUser_NullPassword_ThrowsException() {
            // Arrange
            CreateUserRequest request = createRequest("testuser", "test@example.com", null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                userService.createUser(request);
            });

            verifyNoInteractions(userRepository);
            verifyNoInteractions(passwordEncoder);
        }
    }

    // Helper methods
    private CreateUserRequest createValidRequest() {
        return new CreateUserRequest("testuser", "test@example.com", "password123");
    }

    private CreateUserRequest createRequest(String username, String email, String password) {
        return new CreateUserRequest(username, email, password);
    }
}
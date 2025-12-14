package com.dbexp.db_experiment.service.auth;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.auth.LoginRequest;
import com.dbexp.db_experiment.dto.auth.LoginResponse;
import com.dbexp.db_experiment.dto.auth.LogoutResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.repository.UserRepository;
import com.dbexp.db_experiment.service.AuthServiceImpl;
import com.dbexp.db_experiment.testutil.UserTestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Auth Service - Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("Login Success Tests")
    class LoginSuccessTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void login_Success() {
            // Arrange
            String username = "testuser";
            String password = "password123";
            String encodedPassword = "encodedPassword123";
            Long userId = 1L;
            String email = "test@example.com";

            LoginRequest request = new LoginRequest(username, password);
            User user = UserTestBuilder.aUser()
                    .withUserId(userId)
                    .withUsername(username)
                    .withEmail(email)
                    .withPassword(encodedPassword)
                    .build();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

            // Act
            LoginResponse response = authService.login(request, session);

            // Assert
            assertNotNull(response);
            assertEquals(userId, response.userId());
            assertEquals(username, response.username());
            assertEquals(email, response.email());
            assertEquals("Login successful", response.message());

            verify(userRepository).findByUsername(username);
            verify(passwordEncoder).matches(password, encodedPassword);
            verify(session).setAttribute("userId", userId);
        }
    }

    @Nested
    @DisplayName("Login Failure Tests")
    class LoginFailureTests {

        @Test
        @DisplayName("Should throw exception when user not found")
        void login_UserNotFound() {
            // Arrange
            String username = "nonexistent";
            String password = "password123";
            LoginRequest request = new LoginRequest(username, password);

            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // Act & Assert
            BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
                authService.login(request, session);
            });

            assertEquals("Invalid username or password", exception.getMessage());

            verify(userRepository).findByUsername(username);
            verify(passwordEncoder, never()).matches(any(), any());
            verify(session, never()).setAttribute(any(), any());
        }

        @Test
        @DisplayName("Should throw exception when password is wrong")
        void login_WrongPassword() {
            // Arrange
            String username = "testuser";
            String password = "wrongpassword";
            String encodedPassword = "encodedPassword123";
            Long userId = 1L;
            String email = "test@example.com";

            LoginRequest request = new LoginRequest(username, password);
            User user = UserTestBuilder.aUser()
                    .withUserId(userId)
                    .withUsername(username)
                    .withEmail(email)
                    .withPassword(encodedPassword)
                    .build();

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

            // Act & Assert
            BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
                authService.login(request, session);
            });

            assertEquals("Invalid username or password", exception.getMessage());

            verify(userRepository).findByUsername(username);
            verify(passwordEncoder).matches(password, encodedPassword);
            verify(session, never()).setAttribute(any(), any());
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        void logout_Success() {
            // Act
            LogoutResponse response = authService.logout(session);

            // Assert
            assertNotNull(response);
            assertEquals("Logout successful", response.message());
            verify(session).invalidate();
        }
    }

    @Nested
    @DisplayName("Get Current User Tests")
    class GetCurrentUserTests {

        @Test
        @DisplayName("Should return authenticated user when logged in")
        void getCurrentUser_Authenticated() {
            // Arrange
            Long userId = 1L;
            String username = "testuser";
            String email = "test@example.com";
            User user = UserTestBuilder.aUser()
                    .withUserId(userId)
                    .withUsername(username)
                    .withEmail(email)
                    .withPassword("encodedPassword")
                    .build();

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // Act
            CurrentUserResponse response = authService.getCurrentUser(session);

            // Assert
            assertNotNull(response);
            assertTrue(response.authenticated());
            assertEquals(userId, response.userId());
            assertEquals(username, response.username());
            assertEquals(email, response.email());

            verify(session).getAttribute("userId");
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("Should return not authenticated when not logged in")
        void getCurrentUser_NotAuthenticated() {
            // Arrange
            when(session.getAttribute("userId")).thenReturn(null);

            // Act
            CurrentUserResponse response = authService.getCurrentUser(session);

            // Assert
            assertNotNull(response);
            assertFalse(response.authenticated());
            assertEquals(null, response.userId());
            assertEquals(null, response.username());
            assertEquals(null, response.email());

            verify(session).getAttribute("userId");
            verify(userRepository, never()).findById(any());
        }

        @Test
        @DisplayName("Should return not authenticated when user not found")
        void getCurrentUser_UserNotFound() {
            // Arrange
            Long userId = 1L;

            when(session.getAttribute("userId")).thenReturn(userId);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // Act
            CurrentUserResponse response = authService.getCurrentUser(session);

            // Assert
            assertNotNull(response);
            assertFalse(response.authenticated());
            assertEquals(null, response.userId());
            assertEquals(null, response.username());
            assertEquals(null, response.email());

            verify(session).getAttribute("userId");
            verify(userRepository).findById(userId);
        }
    }
}
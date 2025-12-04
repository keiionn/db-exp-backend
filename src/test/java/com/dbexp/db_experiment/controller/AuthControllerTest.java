package com.dbexp.db_experiment.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.auth.LoginRequest;
import com.dbexp.db_experiment.dto.auth.LoginResponse;
import com.dbexp.db_experiment.dto.auth.LogoutResponse;
import com.dbexp.db_experiment.service.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth Controller - Tests")
class AuthControllerTest extends BaseControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {
        super.setUpBase();
        AuthController authController = new AuthController(authService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Nested
    @DisplayName("Login Success Tests")
    class LoginSuccessTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void login_Success() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "password123");
            LoginResponse response = new LoginResponse(1L, "testuser", "test@example.com", "Login successful");

            when(authService.login(any(LoginRequest.class), any(HttpSession.class))).thenReturn(response);

            // Act & Assert
            performPostRequest("/api/auth/login", request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(1L))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.message").value("Login successful"));
        }
    }

    @Nested
    @DisplayName("Login Validation Tests")
    class LoginValidationTests {

        @Test
        @DisplayName("Should return bad request when username is missing")
        void login_MissingUsername() throws Exception {
            // Arrange
            String invalidRequest = "{\"password\": \"password123\"}";

            // Act & Assert
            performPostRequest("/api/auth/login", invalidRequest)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when password is missing")
        void login_MissingPassword() throws Exception {
            // Arrange
            String invalidRequest = "{\"username\": \"testuser\"}";

            // Act & Assert
            performPostRequest("/api/auth/login", invalidRequest)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when username is empty")
        void login_EmptyUsername() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("", "password123");

            // Act & Assert
            performPostRequest("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when password is empty")
        void login_EmptyPassword() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("testuser", "");

            // Act & Assert
            performPostRequest("/api/auth/login", request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when username is null")
        void login_NullUsername() throws Exception {
            // Arrange
            String invalidRequest = "{\"username\": null, \"password\": \"password123\"}";

            // Act & Assert
            performPostRequest("/api/auth/login", invalidRequest)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request when password is null")
        void login_NullPassword() throws Exception {
            // Arrange
            String invalidRequest = "{\"username\": \"testuser\", \"password\": null}";

            // Act & Assert
            performPostRequest("/api/auth/login", invalidRequest)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Login Authentication Tests")
    class LoginAuthenticationTests {

        @Test
        @DisplayName("Should return unauthorized for invalid credentials")
        void login_InvalidCredentials() throws Exception {
            // Arrange
            LoginRequest request = new LoginRequest("invaliduser", "wrongpassword");

            when(authService.login(any(LoginRequest.class), any(HttpSession.class)))
                    .thenThrow(new org.springframework.security.authentication.BadCredentialsException(
                            "Invalid username or password"));

            // Act & Assert
            performPostRequest("/api/auth/login", request)
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.userId").isEmpty())
                    .andExpect(jsonPath("$.username").isEmpty())
                    .andExpect(jsonPath("$.email").isEmpty())
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should logout successfully")
        void logout_Success() throws Exception {
            // Arrange
            LogoutResponse response = new LogoutResponse("Logout successful");

            when(authService.logout(any(HttpSession.class))).thenReturn(response);

            // Act & Assert
            performPostRequest("/api/auth/logout", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Logout successful"));
        }
    }

    @Nested
    @DisplayName("Current User Tests")
    class CurrentUserTests {

        @Test
        @DisplayName("Should return current user details when authenticated")
        void getCurrentUser_Authenticated() throws Exception {
            // Arrange
            CurrentUserResponse response = new CurrentUserResponse(true, 1L, "testuser", "test@example.com");

            when(authService.getCurrentUser(any(HttpSession.class))).thenReturn(response);

            // Act & Assert
            performGetRequest("/api/auth/me", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authenticated").value(true))
                    .andExpect(jsonPath("$.userId").value(1L))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("Should return not authenticated response when user is not logged in")
        void getCurrentUser_NotAuthenticated() throws Exception {
            // Arrange
            CurrentUserResponse response = CurrentUserResponse.notAuthenticated();

            when(authService.getCurrentUser(any(HttpSession.class))).thenReturn(response);

            // Act & Assert
            performGetRequest("/api/auth/me", null)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.authenticated").value(false))
                    .andExpect(jsonPath("$.userId").isEmpty())
                    .andExpect(jsonPath("$.username").isEmpty())
                    .andExpect(jsonPath("$.email").isEmpty());
        }
    }

}
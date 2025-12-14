package com.dbexp.db_experiment.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpSession;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.ChangeEmailRequest;
import com.dbexp.db_experiment.dto.user.ChangeEmailResponse;
import com.dbexp.db_experiment.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller - Email Change Tests")
class UserControllerEmailTest extends BaseControllerTest {

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .addFilters(new CharacterEncodingFilter("UTF-8", true)) // Ensure proper encoding
                .build();
    }

    @Nested
    @DisplayName("Successful Email Changes")
    class SuccessTests {

        @Test
        @DisplayName("Should change email successfully with valid data")
        void changeEmail_Success() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "new@example.com");
            ChangeEmailResponse response = new ChangeEmailResponse(userId, "old@example.com", "new@example.com",
                    LocalDateTime.now(), "Email changed successfully");

            when(userService.changeEmail(any(HttpSession.class), eq(userId), any(ChangeEmailRequest.class)))
                    .thenReturn(response);

            performPutRequest("/api/users/{userId}/email", userId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.oldEmail").value("old@example.com"))
                    .andExpect(jsonPath("$.newEmail").value("new@example.com"))
                    .andExpect(jsonPath("$.updatedAt").exists())
                    .andExpect(jsonPath("$.message").value("Email changed successfully"));
        }
    }

    @Nested
    @DisplayName("Validation Error Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for blank current password")
        void changeEmail_BlankCurrentPassword() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("", "new@example.com");

            assertBadRequest(performPutRequest("/api/users/{userId}/email", userId, request));
        }

        @Test
        @DisplayName("Should return bad request for blank new email")
        void changeEmail_BlankNewEmail() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "");

            assertBadRequest(performPutRequest("/api/users/{userId}/email", userId, request));
        }

        @Test
        @DisplayName("Should return bad request for invalid email format")
        void changeEmail_InvalidEmailFormat() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "invalid-email");

            assertBadRequest(performPutRequest("/api/users/{userId}/email", userId, request));
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should return bad request when user not found")
        void changeEmail_InvalidUserId() throws Exception {
            Long userId = 999L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "new@example.com");

            when(userService.changeEmail(any(HttpSession.class), eq(userId), any(ChangeEmailRequest.class)))
                    .thenThrow(new IllegalArgumentException("User not found"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/email", userId, request),
                    "User not found");
        }

        @Test
        @DisplayName("Should return bad request when current password is incorrect")
        void changeEmail_IncorrectPassword() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("wrongPassword", "new@example.com");

            when(userService.changeEmail(any(HttpSession.class), eq(userId), any(ChangeEmailRequest.class)))
                    .thenThrow(new IllegalArgumentException("Current password is incorrect"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/email", userId, request),
                    "Current password is incorrect");
        }

        @Test
        @DisplayName("Should return bad request when new email same as current")
        void changeEmail_SameEmail() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "current@example.com");

            when(userService.changeEmail(any(HttpSession.class), eq(userId), any(ChangeEmailRequest.class)))
                    .thenThrow(new IllegalArgumentException("New email must be different from current email"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/email", userId, request),
                    "New email must be different from current email");
        }

        @Test
        @DisplayName("Should return bad request when email already exists")
        void changeEmail_EmailAlreadyExists() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "existing@example.com");

            when(userService.changeEmail(any(HttpSession.class), eq(userId), any(ChangeEmailRequest.class)))
                    .thenThrow(new IllegalArgumentException("Email already exists"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/email", userId, request),
                    "Email already exists");
        }
    }

    @Nested
    @DisplayName("Server Error Tests")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void changeEmail_InternalServerError() throws Exception {
            Long userId = 1L;
            ChangeEmailRequest request = new ChangeEmailRequest("currentPassword", "new@example.com");

            when(userService.changeEmail(any(HttpSession.class), eq(userId), any(ChangeEmailRequest.class)))
                    .thenThrow(new RuntimeException("Unexpected error"));

            assertInternalServerError(performPutRequest("/api/users/{userId}/email", userId, request),
                    "An error occurred while changing email");
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performPutRequest(String url, Long userId, Object request) throws Exception {
        String formattedUrl = url.replace("{userId}", String.valueOf(userId));
        return performPutRequest(formattedUrl, request);
    }
}
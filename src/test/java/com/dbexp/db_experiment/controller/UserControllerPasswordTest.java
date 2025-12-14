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

import com.dbexp.db_experiment.dto.user.ChangePasswordRequest;
import com.dbexp.db_experiment.dto.user.ChangePasswordResponse;
import com.dbexp.db_experiment.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller - Password Change Tests")
class UserControllerPasswordTest extends BaseControllerTest {

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
    @DisplayName("Successful Password Changes")
    class SuccessTests {

        @Test
        @DisplayName("Should change password successfully with valid data")
        void changePassword_Success() throws Exception {
            Long userId = 1L;
            ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "newPassword123");
            ChangePasswordResponse response = new ChangePasswordResponse(userId, LocalDateTime.now(),
                    "Password updated successfully");

            when(userService.changePassword(any(HttpSession.class), eq(userId), any(ChangePasswordRequest.class)))
                    .thenReturn(response);

            performPutRequest("/api/users/{userId}/password", userId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.updatedAt").exists())
                    .andExpect(jsonPath("$.message").value("Password updated successfully"));
        }
    }

    @Nested
    @DisplayName("Validation Error Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for blank current password")
        void changePassword_BlankCurrentPassword() throws Exception {
            Long userId = 1L;
            ChangePasswordRequest request = new ChangePasswordRequest("", "newPassword123");

            assertBadRequest(performPutRequest("/api/users/{userId}/password", userId, request));
        }

        @Test
        @DisplayName("Should return bad request for blank new password")
        void changePassword_BlankNewPassword() throws Exception {
            Long userId = 1L;
            ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "");

            assertBadRequest(performPutRequest("/api/users/{userId}/password", userId, request));
        }

        @Test
        @DisplayName("Should return bad request for too short new password")
        void changePassword_NewPasswordTooShort() throws Exception {
            Long userId = 1L;
            ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "short");

            assertBadRequest(performPutRequest("/api/users/{userId}/password", userId, request));
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should return bad request when user not found")
        void changePassword_InvalidUserId() throws Exception {
            Long userId = 999L;
            ChangePasswordRequest request = new ChangePasswordRequest("currentPassword", "newPassword123");

            when(userService.changePassword(any(HttpSession.class), eq(userId), any(ChangePasswordRequest.class)))
                    .thenThrow(new IllegalArgumentException("User not found"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/password", userId, request),
                    "User not found");
        }

        @Test
        @DisplayName("Should return bad request when current password is incorrect")
        void changePassword_IncorrectPassword() throws Exception {
            Long userId = 1L;
            ChangePasswordRequest request = new ChangePasswordRequest("wrongPassword", "newPassword123");

            when(userService.changePassword(any(HttpSession.class), eq(userId), any(ChangePasswordRequest.class)))
                    .thenThrow(new IllegalArgumentException("Current password is incorrect"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/password", userId, request),
                    "Current password is incorrect");
        }

        @Test
        @DisplayName("Should return bad request when new password same as current")
        void changePassword_SamePassword() throws Exception {
            Long userId = 1L;
            ChangePasswordRequest request = new ChangePasswordRequest("samePassword", "samePassword");

            when(userService.changePassword(any(HttpSession.class), eq(userId), any(ChangePasswordRequest.class)))
                    .thenThrow(new IllegalArgumentException("New password must be different from current password"));

            assertBadRequest(performPutRequest("/api/users/{userId}/password", userId, request));
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performPutRequest(String url, Long userId, Object request) throws Exception {
        String formattedUrl = url.replace("{userId}", String.valueOf(userId));
        return performPutRequest(formattedUrl, request);
    }
}
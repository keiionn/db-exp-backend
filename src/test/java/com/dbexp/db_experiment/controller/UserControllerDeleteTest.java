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

import com.dbexp.db_experiment.dto.user.DeleteAccountRequest;
import com.dbexp.db_experiment.dto.user.DeleteAccountResponse;
import com.dbexp.db_experiment.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller - Account Deletion Tests")
class UserControllerDeleteTest extends BaseControllerTest {

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
    @DisplayName("Successful Account Deletions")
    class SuccessTests {

        @Test
        @DisplayName("Should delete account successfully with valid password")
        void deleteAccount_Success() throws Exception {
            Long userId = 1L;
            DeleteAccountRequest request = new DeleteAccountRequest("currentPassword123");
            DeleteAccountResponse response = new DeleteAccountResponse(userId, LocalDateTime.now(),
                    "Account deleted successfully");

            when(userService.deleteAccount(any(HttpSession.class), eq(userId), any(DeleteAccountRequest.class)))
                    .thenReturn(response);

            performDeleteRequest("/api/users/{userId}", userId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.deletedAt").exists())
                    .andExpect(jsonPath("$.message").value("Account deleted successfully"));
        }
    }

    @Nested
    @DisplayName("Validation Error Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for blank password")
        void deleteAccount_BlankPassword() throws Exception {
            Long userId = 1L;
            DeleteAccountRequest request = new DeleteAccountRequest("");

            assertBadRequest(performDeleteRequest("/api/users/{userId}", userId, request));
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should return bad request when user not found")
        void deleteAccount_InvalidUserId() throws Exception {
            Long userId = 999L;
            DeleteAccountRequest request = new DeleteAccountRequest("currentPassword123");

            when(userService.deleteAccount(any(HttpSession.class), eq(userId), any(DeleteAccountRequest.class)))
                    .thenThrow(new IllegalArgumentException("User not found"));

            assertBadRequestWithMessage(performDeleteRequest("/api/users/{userId}", userId, request), "User not found");
        }

        @Test
        @DisplayName("Should return bad request when password is incorrect")
        void deleteAccount_IncorrectPassword() throws Exception {
            Long userId = 1L;
            DeleteAccountRequest request = new DeleteAccountRequest("wrongPassword");

            when(userService.deleteAccount(any(HttpSession.class), eq(userId), any(DeleteAccountRequest.class)))
                    .thenThrow(new IllegalArgumentException("Current password is incorrect"));

            assertBadRequestWithMessage(performDeleteRequest("/api/users/{userId}", userId, request),
                    "Current password is incorrect");
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performDeleteRequest(String url, Long userId, Object request) throws Exception {
        String formattedUrl = url.replace("{userId}", String.valueOf(userId));
        return performDeleteRequest(formattedUrl, request);
    }
}
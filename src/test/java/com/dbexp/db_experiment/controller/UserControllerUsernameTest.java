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

import com.dbexp.db_experiment.dto.user.ChangeUsernameRequest;
import com.dbexp.db_experiment.dto.user.ChangeUsernameResponse;
import com.dbexp.db_experiment.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller - Username Change Tests")
class UserControllerUsernameTest extends BaseControllerTest {

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
    @DisplayName("Successful Username Changes")
    class SuccessTests {

        @Test
        @DisplayName("Should change username successfully with valid data")
        void changeUsername_Success() throws Exception {
            Long userId = 1L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("newusername");
            ChangeUsernameResponse response = new ChangeUsernameResponse(userId, "oldusername", "newusername",
                    LocalDateTime.now());

            when(userService.changeUsername(any(HttpSession.class), eq(userId), any(ChangeUsernameRequest.class)))
                    .thenReturn(response);

            performPutRequest("/api/users/{userId}/username", userId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.oldUsername").value("oldusername"))
                    .andExpect(jsonPath("$.newUsername").value("newusername"))
                    .andExpect(jsonPath("$.updatedAt").exists());
        }
    }

    @Nested
    @DisplayName("Validation Error Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for invalid username format")
        void changeUsername_InvalidFormat_ReturnsBadRequest() throws Exception {
            Long userId = 1L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("user@name!");

            assertBadRequest(performPutRequest("/api/users/{userId}/username", userId, request));
        }

        @Test
        @DisplayName("Should return bad request for too short username")
        void changeUsername_TooShort_ReturnsBadRequest() throws Exception {
            Long userId = 1L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("ab");

            assertBadRequest(performPutRequest("/api/users/{userId}/username", userId, request));
        }

        @Test
        @DisplayName("Should return bad request for too long username")
        void changeUsername_TooLong_ReturnsBadRequest() throws Exception {
            Long userId = 1L;
            String longUsername = "a".repeat(256);
            ChangeUsernameRequest request = new ChangeUsernameRequest(longUsername);

            assertBadRequest(performPutRequest("/api/users/{userId}/username", userId, request));
        }

        @Test
        @DisplayName("Should return bad request for blank username")
        void changeUsername_BlankUsername_ReturnsBadRequest() throws Exception {
            Long userId = 1L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("");

            assertBadRequest(performPutRequest("/api/users/{userId}/username", userId, request));
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should return bad request when user not found")
        void changeUsername_InvalidUserId_ReturnsBadRequest() throws Exception {
            Long userId = 999L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("newusername");

            when(userService.changeUsername(any(HttpSession.class), eq(userId), any(ChangeUsernameRequest.class)))
                    .thenThrow(new IllegalArgumentException("User not found"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/username", userId, request),
                    "User not found");
        }

        @Test
        @DisplayName("Should return bad request when username already exists")
        void changeUsername_DuplicateUsername_ReturnsBadRequest() throws Exception {
            Long userId = 1L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("existinguser");

            when(userService.changeUsername(any(HttpSession.class), eq(userId), any(ChangeUsernameRequest.class)))
                    .thenThrow(new IllegalArgumentException("Username already exists"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/username", userId, request),
                    "Username already exists");
        }

        @Test
        @DisplayName("Should return bad request when new username same as current")
        void changeUsername_SameUsername_ReturnsBadRequest() throws Exception {
            Long userId = 1L;
            ChangeUsernameRequest request = new ChangeUsernameRequest("currentusername");

            when(userService.changeUsername(any(HttpSession.class), eq(userId), any(ChangeUsernameRequest.class)))
                    .thenThrow(new IllegalArgumentException("New username must be different from current username"));

            assertBadRequestWithMessage(performPutRequest("/api/users/{userId}/username", userId, request),
                    "New username must be different from current username");
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performPutRequest(String url, Long userId, Object request) throws Exception {
        String formattedUrl = url.replace("{userId}", String.valueOf(userId));
        return performPutRequest(formattedUrl, request);
    }
}
package com.dbexp.db_experiment.controller;

import java.time.LocalDateTime;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.user.GetUserByUsernameRequest;
import com.dbexp.db_experiment.dto.user.GetUserByUsernameResponse;
import com.dbexp.db_experiment.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Controller - Get User By Username Tests")
class UserControllerGetUserByUsernameTest extends BaseControllerTest {

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Nested
    @DisplayName("Successful User Retrievals by Username")
    class SuccessTests {

        @Test
        @DisplayName("Should retrieve user successfully with valid username")
        void getUserByUsername_Success() throws Exception {
            String username = "testuser";
            GetUserByUsernameRequest request = new GetUserByUsernameRequest(username);
            GetUserByUsernameResponse response = new GetUserByUsernameResponse(1L, username, "test@example.com",
                    LocalDateTime.now());

            when(userService.getUserByUsername(any(GetUserByUsernameRequest.class))).thenReturn(response);

            performGetRequest("/api/users/username/{username}", username, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(1L))
                    .andExpect(jsonPath("$.username").value(username))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.creationTime").exists());
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should return bad request when user not found")
        void getUserByUsername_UserNotFound() throws Exception {
            String username = "nonexistent";
            GetUserByUsernameRequest request = new GetUserByUsernameRequest(username);

            when(userService.getUserByUsername(any(GetUserByUsernameRequest.class)))
                    .thenThrow(new IllegalArgumentException("User not found"));

            assertBadRequestWithMessage(performGetRequest("/api/users/username/{username}", username, request),
                    "User not found");
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performGetRequest(String url, String username, Object request) throws Exception {
        String formattedUrl = url.replace("{username}", username != null ? username : "");
        return performGetRequest(formattedUrl, request);
    }
}
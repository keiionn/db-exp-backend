package com.dbexp.db_experiment.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpSession;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.community.ChangeDescriptionRequest;
import com.dbexp.db_experiment.dto.community.ChangeDescriptionResponse;
import com.dbexp.db_experiment.service.CommunityService;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Controller - Change Description Tests")
class CommunityControllerDescriptionTest extends BaseCommunityControllerTest {

    @Mock
    private CommunityService communityService;
    
    @Mock
    private PostService postService;

    @BeforeEach
    void setUp() {
        CommunityController communityController = new CommunityController(communityService, postService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(communityController).build();
    }

    @Nested
    @DisplayName("Successful Description Changes")
    class SuccessTests {

        @Test
        @DisplayName("Should change description successfully with valid data")
        void changeDescription_Success() throws Exception {
            Long communityId = 1L;
            String newDescription = "New Description";
            ChangeDescriptionRequest request = new ChangeDescriptionRequest(newDescription);
            ChangeDescriptionResponse response = new ChangeDescriptionResponse(communityId, "Old Description",
                    newDescription, LocalDateTime.now());

            when(communityService.changeDescription(any(HttpSession.class), any(Long.class),
                    any(ChangeDescriptionRequest.class)))
                    .thenReturn(response);

            performPutRequest("/api/communities/{communityId}/description", communityId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.communityId").value(communityId))
                    .andExpect(jsonPath("$.oldDescription").value("Old Description"))
                    .andExpect(jsonPath("$.newDescription").value(newDescription))
                    .andExpect(jsonPath("$.changeTime").exists());
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should return bad request when community not found")
        void changeDescription_CommunityNotFound() throws Exception {
            Long communityId = 999L;
            ChangeDescriptionRequest request = new ChangeDescriptionRequest("New Description");

            when(communityService.changeDescription(any(HttpSession.class), any(Long.class),
                    any(ChangeDescriptionRequest.class)))
                    .thenThrow(new IllegalArgumentException("Community not found"));

            assertBadRequestWithMessage(
                    performPutRequest("/api/communities/{communityId}/description", communityId, request),
                    "Community not found");
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performPutRequest(String url, Long communityId, Object request) throws Exception {
        String formattedUrl = url.replace("{communityId}", String.valueOf(communityId));
        return performPutRequest(formattedUrl, request);
    }
}
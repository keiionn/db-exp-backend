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

import com.dbexp.db_experiment.dto.community.GetCommunityByIdRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdResponse;
import com.dbexp.db_experiment.service.CommunityService;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Controller - Get Community By ID Tests")
class CommunityControllerGetByIdTest extends BaseCommunityControllerTest {

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
    @DisplayName("Successful Community Retrievals by ID")
    class SuccessTests {

        @Test
        @DisplayName("Should retrieve community successfully with valid ID")
        void getCommunityById_Success() throws Exception {
            Long communityId = 1L;
            GetCommunityByIdRequest request = new GetCommunityByIdRequest(communityId);
            GetCommunityByIdResponse response = new GetCommunityByIdResponse(communityId, "testcommunity",
                    "Test Description", LocalDateTime.now());

            when(communityService.getCommunityById(any(GetCommunityByIdRequest.class))).thenReturn(response);

            performGetRequest("/api/communities/{communityId}", communityId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.communityId").value(communityId))
                    .andExpect(jsonPath("$.name").value("testcommunity"))
                    .andExpect(jsonPath("$.description").value("Test Description"))
                    .andExpect(jsonPath("$.creationTime").exists());
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should return bad request when community not found")
        void getCommunityById_InvalidCommunityId() throws Exception {
            Long communityId = 999L;
            GetCommunityByIdRequest request = new GetCommunityByIdRequest(communityId);

            when(communityService.getCommunityById(any(GetCommunityByIdRequest.class)))
                    .thenThrow(new IllegalArgumentException("Community not found"));

            assertBadRequestWithMessage(performGetRequest("/api/communities/{communityId}", communityId, request),
                    "Community not found");
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performGetRequest(String url, Long communityId, Object request) throws Exception {
        String formattedUrl = url.replace("{communityId}", String.valueOf(communityId));
        return performGetRequest(formattedUrl, request);
    }
}
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

import com.dbexp.db_experiment.dto.community.GetCommunityByNameRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameResponse;
import com.dbexp.db_experiment.service.CommunityService;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Controller - Get Community By Name Tests")
class CommunityControllerGetByNameTest extends BaseCommunityControllerTest {

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
    @DisplayName("Successful Community Retrievals by Name")
    class SuccessTests {

        @Test
        @DisplayName("Should retrieve community successfully with valid name")
        void getCommunityByName_Success() throws Exception {
            String name = "testcommunity";
            GetCommunityByNameRequest request = new GetCommunityByNameRequest(name);
            GetCommunityByNameResponse response = new GetCommunityByNameResponse(1L, name,
                    "Test Description", LocalDateTime.now());

            when(communityService.getCommunityByName(any(GetCommunityByNameRequest.class))).thenReturn(response);

            performGetRequest("/api/communities/name/{name}", name, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.communityId").value(1L))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.description").value("Test Description"))
                    .andExpect(jsonPath("$.creationTime").exists());
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should return bad request when community not found")
        void getCommunityByName_InvalidName() throws Exception {
            String name = "nonexistent";
            GetCommunityByNameRequest request = new GetCommunityByNameRequest(name);

            when(communityService.getCommunityByName(any(GetCommunityByNameRequest.class)))
                    .thenThrow(new IllegalArgumentException("Community not found"));

            assertBadRequestWithMessage(performGetRequest("/api/communities/name/{name}", name, request),
                    "Community not found");
        }
    }

    // Helper method to handle URL path variables
    private ResultActions performGetRequest(String url, String name, Object request) throws Exception {
        String formattedUrl = url.replace("{name}", name);
        return performGetRequest(formattedUrl, request);
    }
}
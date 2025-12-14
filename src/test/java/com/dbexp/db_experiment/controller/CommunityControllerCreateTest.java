package com.dbexp.db_experiment.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpSession;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.community.CreateCommunityRequest;
import com.dbexp.db_experiment.dto.community.CreateCommunityResponse;
import com.dbexp.db_experiment.service.CommunityService;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Community Controller - Create Community Tests")
class CommunityControllerCreateTest extends BaseCommunityControllerTest {

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
    @DisplayName("Successful Community Creations")
    class SuccessTests {

        @Test
        @DisplayName("Should create community successfully with valid data")
        void createCommunity_Success() throws Exception {
            String name = "testcommunity";
            String description = "Test Description";
            CreateCommunityRequest request = new CreateCommunityRequest(name, description);
            CreateCommunityResponse response = new CreateCommunityResponse(1L, name, description,
                    LocalDateTime.now());

            when(communityService.createCommunity(any(HttpSession.class), any(CreateCommunityRequest.class)))
                    .thenReturn(response);

            performPostRequest("/api/communities", request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.communityId").value(1L))
                    .andExpect(jsonPath("$.name").value(name))
                    .andExpect(jsonPath("$.description").value(description))
                    .andExpect(jsonPath("$.creationTime").exists());
        }
    }

    @Nested
    @DisplayName("Business Logic Error Tests")
    class BusinessLogicErrorTests {

        @Test
        @DisplayName("Should return bad request when community name already exists")
        void createCommunity_ExistingName() throws Exception {
            String name = "existingcommunity";
            CreateCommunityRequest request = new CreateCommunityRequest(name, "Test Description");

            when(communityService.createCommunity(any(HttpSession.class), any(CreateCommunityRequest.class)))
                    .thenThrow(new IllegalArgumentException("Community name already exists"));

            assertBadRequestWithMessage(performPostRequest("/api/communities", request),
                    "Community name already exists");
        }
    }
}
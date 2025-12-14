package com.dbexp.db_experiment.controller;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.post.GetPostByIdRequest;
import com.dbexp.db_experiment.dto.post.GetPostByIdResponse;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Controller - Get Post By ID Tests")
class PostControllerGetByIdTest {

    @Mock
    private PostService postService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        PostController postController = new PostController(postService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Nested
    @DisplayName("Successful Retrieval")
    class SuccessTests {

        @Test
        @DisplayName("Should retrieve post successfully with valid ID")
        void getPostById_Success() throws Exception {
            // Arrange
            Long postId = 1L;
            GetPostByIdResponse response = createSuccessResponse(postId);

            when(postService.getPostById(any(GetPostByIdRequest.class))).thenReturn(response);

            // Act & Assert
            performGetPostByIdRequest(postId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.postId").value(response.getPostId()))
                    .andExpect(jsonPath("$.userId").value(response.getUserId()))
                    .andExpect(jsonPath("$.communityId").value(response.getCommunityId()))
                    .andExpect(jsonPath("$.postTitle").value(response.getPostTitle()))
                    .andExpect(jsonPath("$.postContent").value(response.getPostContent()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should return bad request when post ID does not exist")
        void getPostById_NotFound_ReturnsBadRequest() throws Exception {
            Long postId = 999L;

            when(postService.getPostById(any(GetPostByIdRequest.class)))
                    .thenThrow(new IllegalArgumentException("Post not found"));

            performGetPostByIdRequest(postId)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Post not found"));
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void getPostById_InternalServerError() throws Exception {
            Long postId = 1L;

            when(postService.getPostById(any(GetPostByIdRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performGetPostByIdRequest(postId)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while fetching the post"));
        }
    }

    // Helper methods
    private ResultActions performGetPostByIdRequest(Long postId) throws Exception {
        return mockMvc.perform(get("/api/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private GetPostByIdResponse createSuccessResponse(Long postId) {
        return new GetPostByIdResponse(postId, 1L, 1L, "Test Post Title", "Test Post Content",
                java.time.LocalDateTime.now());
    }
}
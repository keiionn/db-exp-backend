package com.dbexp.db_experiment.controller;

import jakarta.servlet.http.HttpSession;

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

import com.dbexp.db_experiment.dto.post.CreatePostRequest;
import com.dbexp.db_experiment.dto.post.CreatePostResponse;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Controller - Create Post Tests")
class PostControllerCreateTest {

    @Mock
    private PostService postService;

    @Mock
    private HttpSession httpSession;

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
    @DisplayName("Successful Creation")
    class SuccessTests {

        @Test
        @DisplayName("Should create post successfully with valid data")
        void createPost_Success() throws Exception {
            // Arrange
            CreatePostRequest request = createValidRequest();
            CreatePostResponse response = createSuccessResponse();

            when(postService.createPost(any(HttpSession.class), any(CreatePostRequest.class))).thenReturn(response);

            // Act & Assert
            performCreatePostRequest(request)
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.postId").value(response.getPostId()))
                    .andExpect(jsonPath("$.userId").value(response.getUserId()))
                    .andExpect(jsonPath("$.communityId").value(response.getCommunityId()))
                    .andExpect(jsonPath("$.postTitle").value(response.getPostTitle()))
                    .andExpect(jsonPath("$.postContent").value(response.getPostContent()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for empty post title")
        void createPost_EmptyPostTitle_ReturnsBadRequest() throws Exception {
            CreatePostRequest request = createRequest(1L, "", "Test Content");

            performCreatePostRequest(request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request for empty post content")
        void createPost_EmptyPostContent_ReturnsBadRequest() throws Exception {
            CreatePostRequest request = createRequest(1L, "Test Title", "");

            performCreatePostRequest(request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request for null user ID")
        void createPost_NullUserId_ReturnsBadRequest() throws Exception {
            CreatePostRequest request = createRequest(null, "Test Title", "Test Content");

            performCreatePostRequest(request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request for null community ID")
        void createPost_NullCommunityId_ReturnsBadRequest() throws Exception {
            CreatePostRequest request = createRequest(null, "Test Title", "Test Content");

            performCreatePostRequest(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Business Logic Errors")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should return bad request when post creation fails")
        void createPost_BusinessLogicError_ReturnsBadRequest() throws Exception {
            CreatePostRequest request = createValidRequest();

            when(postService.createPost(any(HttpSession.class), any(CreatePostRequest.class)))
                    .thenThrow(new IllegalArgumentException("Invalid post data"));

            performCreatePostRequest(request)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid post data"));
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void createPost_InternalServerError() throws Exception {
            CreatePostRequest request = createValidRequest();

            when(postService.createPost(any(HttpSession.class), any(CreatePostRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performCreatePostRequest(request)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while creating the post"));
        }
    }

    // Helper methods
    private ResultActions performCreatePostRequest(CreatePostRequest request) throws Exception {
        return mockMvc.perform(post("/api/posts")
                .sessionAttr("session", httpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private CreatePostRequest createValidRequest() {
        return new CreatePostRequest(1L, "Test Post Title", "Test Post Content");
    }

    private CreatePostRequest createRequest(Long communityId, String postTitle, String postContent) {
        return new CreatePostRequest(communityId, postTitle, postContent);
    }

    private CreatePostResponse createSuccessResponse() {
        return new CreatePostResponse(1L, 1L, 1L, "Test Post Title", "Test Post Content",
                java.time.LocalDateTime.now());
    }
}
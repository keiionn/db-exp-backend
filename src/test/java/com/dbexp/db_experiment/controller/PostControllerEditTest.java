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

import com.dbexp.db_experiment.dto.post.EditPostRequest;
import com.dbexp.db_experiment.dto.post.EditPostResponse;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Controller - Edit Post Tests")
class PostControllerEditTest {

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
    @DisplayName("Successful Edit")
    class SuccessTests {

        @Test
        @DisplayName("Should edit post successfully with valid data")
        void editPost_Success() throws Exception {
            // Arrange
            Long postId = 1L;
            EditPostRequest request = createValidRequest();
            EditPostResponse response = createSuccessResponse(postId);

            when(postService.editPost(any(), eq(postId), any(EditPostRequest.class))).thenReturn(response);

            // Act & Assert
            performEditPostRequest(postId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.postId").value(response.getPostId()))
                    .andExpect(jsonPath("$.oldPostTitle").value(response.getOldPostTitle()))
                    .andExpect(jsonPath("$.newPostTitle").value(response.getNewPostTitle()))
                    .andExpect(jsonPath("$.oldPostContent").value(response.getOldPostContent()))
                    .andExpect(jsonPath("$.newPostContent").value(response.getNewPostContent()))
                    .andExpect(jsonPath("$.updatedAt").exists());
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for empty post title")
        void editPost_EmptyPostTitle_ReturnsBadRequest() throws Exception {
            Long postId = 1L;
            EditPostRequest request = createRequest("", "New Content");

            performEditPostRequest(postId, request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request for empty post content")
        void editPost_EmptyPostContent_ReturnsBadRequest() throws Exception {
            Long postId = 1L;
            EditPostRequest request = createRequest("New Title", "");

            performEditPostRequest(postId, request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should return bad request when post ID does not exist")
        void editPost_NotFound_ReturnsBadRequest() throws Exception {
            Long postId = 999L;
            EditPostRequest request = createValidRequest();

            when(postService.editPost(any(), eq(postId), any(EditPostRequest.class)))
                    .thenThrow(new IllegalArgumentException("Post not found"));

            performEditPostRequest(postId, request)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Post not found"));
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void editPost_InternalServerError() throws Exception {
            Long postId = 1L;
            EditPostRequest request = createValidRequest();

            when(postService.editPost(any(), eq(postId), any(EditPostRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performEditPostRequest(postId, request)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while editing the post"));
        }
    }

    // Helper methods
    private ResultActions performEditPostRequest(Long postId, EditPostRequest request) throws Exception {
        return mockMvc.perform(put("/api/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private EditPostRequest createValidRequest() {
        return new EditPostRequest("New Post Title", "New Post Content");
    }

    private EditPostRequest createRequest(String postTitle, String postContent) {
        return new EditPostRequest(postTitle, postContent);
    }

    private EditPostResponse createSuccessResponse(Long postId) {
        return new EditPostResponse(postId, "Old Title", "New Title", "Old Content", "New Content",
                java.time.LocalDateTime.now());
    }
}
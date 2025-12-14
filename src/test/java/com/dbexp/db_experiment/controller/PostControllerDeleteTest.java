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

import com.dbexp.db_experiment.dto.post.DeletePostResponse;
import com.dbexp.db_experiment.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Controller - Delete Post Tests")
class PostControllerDeleteTest {

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
    @DisplayName("Successful Deletion")
    class SuccessTests {

        @Test
        @DisplayName("Should delete post successfully with valid ID")
        void deletePost_Success() throws Exception {
            // Arrange
            Long postId = 1L;
            DeletePostResponse response = createSuccessResponse(postId);

            when(postService.deletePost(any(), eq(postId))).thenReturn(response);

            // Act & Assert
            performDeletePostRequest(postId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.postId").value(response.getPostId()))
                    .andExpect(jsonPath("$.deletedAt").exists())
                    .andExpect(jsonPath("$.message").value(response.getMessage()));
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should return bad request when post ID does not exist")
        void deletePost_NotFound_ReturnsBadRequest() throws Exception {
            Long postId = 999L;

            when(postService.deletePost(any(), eq(postId)))
                    .thenThrow(new IllegalArgumentException("Post not found"));

            performDeletePostRequest(postId)
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Post not found"));
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void deletePost_InternalServerError() throws Exception {
            Long postId = 1L;

            when(postService.deletePost(any(), eq(postId)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performDeletePostRequest(postId)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while deleting the post"));
        }
    }

    // Helper methods
    private ResultActions performDeletePostRequest(Long postId) throws Exception {
        return mockMvc.perform(delete("/api/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private DeletePostResponse createSuccessResponse(Long postId) {
        return new DeletePostResponse(postId, java.time.LocalDateTime.now(), "Post deleted successfully");
    }
}
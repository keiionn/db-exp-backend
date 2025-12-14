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

import com.dbexp.db_experiment.dto.comment.GetCommentByIdResponse;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.service.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Comment Controller - Get Comment By ID Tests")
class CommentControllerGetByIdTest {

    @Mock
    private CommentService commentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        CommentController commentController = new CommentController(commentService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Nested
    @DisplayName("Successful Retrieval")
    class SuccessTests {

        @Test
        @DisplayName("Should retrieve comment successfully with valid ID")
        void getCommentById_Success() throws Exception {
            // Arrange
            Long commentId = 1L;
            GetCommentByIdResponse response = createSuccessResponse(commentId);

            when(commentService.getById(any())).thenReturn(response);

            // Act & Assert
            performGetCommentByIdRequest(commentId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.commentId").value(response.getCommentId()))
                    .andExpect(jsonPath("$.userId").value(response.getUserId()))
                    .andExpect(jsonPath("$.postId").value(response.getPostId()))
                    .andExpect(jsonPath("$.parentCommentId").value(response.getParentCommentId()))
                    .andExpect(jsonPath("$.content").value(response.getContent()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should return not found when comment ID does not exist")
        void getCommentById_NotFound_ReturnsNotFound() throws Exception {
            Long commentId = 999L;

            when(commentService.getById(any()))
                    .thenThrow(new ResourceNotFoundException("Comment not found"));

            performGetCommentByIdRequest(commentId)
                    .andExpect(status().isNotFound())
                    .andExpect(content().string("Comment not found"));
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void getCommentById_InternalServerError() throws Exception {
            Long commentId = 1L;

            when(commentService.getById(any()))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performGetCommentByIdRequest(commentId)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while fetching the comment"));
        }
    }

    // Helper methods
    private ResultActions performGetCommentByIdRequest(Long commentId) throws Exception {
        return mockMvc.perform(get("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private GetCommentByIdResponse createSuccessResponse(Long commentId) {
        return new GetCommentByIdResponse(commentId, "Test Comment Content", 1L, 1L, null,
                java.time.LocalDateTime.now());
    }
}
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

import com.dbexp.db_experiment.dto.comment.DeleteCommentResponse;
import com.dbexp.db_experiment.service.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Comment Controller - Delete Comment Tests")
class CommentControllerDeleteTest {

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
    @DisplayName("Successful Deletion")
    class SuccessTests {

        @Test
        @DisplayName("Should delete comment successfully with valid data")
        void deleteComment_Success() throws Exception {
            // Arrange
            Long commentId = 1L;
            DeleteCommentResponse response = createSuccessResponse(commentId);

            when(commentService.delete(any(HttpSession.class), anyLong())).thenReturn(response);

            // Act & Assert
            performDeleteCommentRequest(commentId)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.commentId").value(response.getCommentId()))
                    .andExpect(jsonPath("$.deletedAt").exists())
                    .andExpect(jsonPath("$.message").value(response.getMessage()));
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    class ValidationTests {

        @Test
        @DisplayName("Should return not found for null comment ID in request")
        void deleteComment_NullCommentIdInRequest_ReturnsNotFound() throws Exception {
            performDeleteCommentRequest(null)
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void deleteComment_InternalServerError() throws Exception {
            Long commentId = 1L;

            when(commentService.delete(any(HttpSession.class), anyLong()))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performDeleteCommentRequest(commentId)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while deleting the comment"));
        }
    }

    // Helper methods
    private ResultActions performDeleteCommentRequest(Long commentId) throws Exception {
        return mockMvc.perform(delete("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private DeleteCommentResponse createSuccessResponse(Long commentId) {
        return new DeleteCommentResponse(commentId, java.time.LocalDateTime.now(),
                "Comment deleted successfully");
    }
}
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

import com.dbexp.db_experiment.dto.comment.EditCommentRequest;
import com.dbexp.db_experiment.dto.comment.EditCommentResponse;
import com.dbexp.db_experiment.service.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Comment Controller - Edit Comment Tests")
class CommentControllerEditTest {

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
    @DisplayName("Successful Edit")
    class SuccessTests {

        @Test
        @DisplayName("Should edit comment successfully with valid data")
        void editComment_Success() throws Exception {
            // Arrange
            Long commentId = 1L;
            EditCommentRequest request = createValidRequest();
            EditCommentResponse response = createSuccessResponse(commentId);

            when(commentService.edit(any(HttpSession.class), anyLong(), any(EditCommentRequest.class)))
                    .thenReturn(response);

            // Act & Assert
            performEditCommentRequest(commentId, request)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.commentId").value(response.getCommentId()))
                    .andExpect(jsonPath("$.oldContent").value(response.getOldContent()))
                    .andExpect(jsonPath("$.newContent").value(response.getNewContent()))
                    .andExpect(jsonPath("$.editedAt").exists());
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for empty comment content")
        void editComment_EmptyContent_ReturnsBadRequest() throws Exception {
            Long commentId = 1L;
            EditCommentRequest request = createRequest("");

            performEditCommentRequest(commentId, request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return not found for null comment ID in request")
        void editComment_NullCommentIdInRequest_ReturnsNotFound() throws Exception {
            EditCommentRequest request = createRequest("New Content");

            performEditCommentRequest(null, request)
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return bad request for content exceeding max length")
        void editComment_ContentTooLong_ReturnsBadRequest() throws Exception {
            Long commentId = 1L;
            String longContent = "a".repeat(1001);
            EditCommentRequest request = createRequest(longContent);

            performEditCommentRequest(commentId, request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception")
        void editComment_InternalServerError() throws Exception {
            Long commentId = 1L;
            EditCommentRequest request = createValidRequest();

            when(commentService.edit(any(HttpSession.class), anyLong(), any(EditCommentRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performEditCommentRequest(commentId, request)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while editing the comment"));
        }
    }

    // Helper methods
    private ResultActions performEditCommentRequest(Long commentId, EditCommentRequest request) throws Exception {
        return mockMvc.perform(put("/api/comments/{commentId}", commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private EditCommentRequest createValidRequest() {
        return new EditCommentRequest("New Comment Content");
    }

    private EditCommentRequest createRequest(String content) {
        return new EditCommentRequest(content);
    }

    private EditCommentResponse createSuccessResponse(Long commentId) {
        return new EditCommentResponse(commentId, "Old Comment Content", "New Comment Content",
                java.time.LocalDateTime.now());
    }
}
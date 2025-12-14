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

import com.dbexp.db_experiment.dto.comment.CreateCommentRequest;
import com.dbexp.db_experiment.dto.comment.CreateCommentResponse;
import com.dbexp.db_experiment.service.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("Comment Controller - Create Comment Tests")
class CommentControllerCreateTest {

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
    @DisplayName("Successful Creation")
    class SuccessTests {

        @Test
        @DisplayName("Should create comment under post successfully with valid data")
        void createCommentUnderPost_Success() throws Exception {
            // Arrange
            CreateCommentRequest request = createValidRequestUnderPost();
            CreateCommentResponse response = createSuccessResponseUnderPost();

            when(commentService.createUnderPost(any(HttpSession.class), any(CreateCommentRequest.class)))
                    .thenReturn(response);

            // Act & Assert
            performCreateCommentRequest(request)
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/api/comments/1"))
                    .andExpect(jsonPath("$.commentId").value(response.getCommentId()))
                    .andExpect(jsonPath("$.userId").value(response.getUserId()))
                    .andExpect(jsonPath("$.postId").value(response.getPostId()))
                    .andExpect(jsonPath("$.parentCommentId").isEmpty())
                    .andExpect(jsonPath("$.content").value(response.getContent()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("Should create comment under comment successfully with valid data")
        void createCommentUnderComment_Success() throws Exception {
            // Arrange
            CreateCommentRequest request = createValidRequestUnderComment();
            CreateCommentResponse response = createSuccessResponseUnderComment();

            when(commentService.createUnderComment(any(HttpSession.class), any(CreateCommentRequest.class)))
                    .thenReturn(response);

            // Act & Assert
            performCreateCommentRequest(request)
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/api/comments/2"))
                    .andExpect(jsonPath("$.commentId").value(response.getCommentId()))
                    .andExpect(jsonPath("$.userId").value(response.getUserId()))
                    .andExpect(jsonPath("$.postId").value(response.getPostId()))
                    .andExpect(jsonPath("$.parentCommentId").value(response.getParentCommentId()))
                    .andExpect(jsonPath("$.content").value(response.getContent()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }
    }

    @Nested
    @DisplayName("Validation Errors")
    class ValidationTests {

        @Test
        @DisplayName("Should return bad request for empty comment content")
        void createComment_EmptyContent_ReturnsBadRequest() throws Exception {
            CreateCommentRequest request = createRequest(1L, null, "");

            performCreateCommentRequest(request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request for null post ID")
        void createComment_NullPostId_ReturnsBadRequest() throws Exception {
            CreateCommentRequest request = createRequest(null, null, "Test Content");

            performCreateCommentRequest(request)
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return bad request for content exceeding max length")
        void createComment_ContentTooLong_ReturnsBadRequest() throws Exception {
            String longContent = "a".repeat(1001);
            CreateCommentRequest request = createRequest(1L, null, longContent);

            performCreateCommentRequest(request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorTests {

        @Test
        @DisplayName("Should return internal server error on unexpected exception for create under post")
        void createCommentUnderPost_InternalServerError() throws Exception {
            CreateCommentRequest request = createValidRequestUnderPost();

            when(commentService.createUnderPost(any(HttpSession.class), any(CreateCommentRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performCreateCommentRequest(request)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while creating the comment"));
        }

        @Test
        @DisplayName("Should return internal server error on unexpected exception for create under comment")
        void createCommentUnderComment_InternalServerError() throws Exception {
            CreateCommentRequest request = createValidRequestUnderComment();

            when(commentService.createUnderComment(any(HttpSession.class), any(CreateCommentRequest.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            performCreateCommentRequest(request)
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("An error occurred while creating the comment"));
        }
    }

    // Helper methods
    private ResultActions performCreateCommentRequest(CreateCommentRequest request) throws Exception {
        return mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private CreateCommentRequest createValidRequestUnderPost() {
        return new CreateCommentRequest(1L, null, "Test Comment Content");
    }

    private CreateCommentRequest createValidRequestUnderComment() {
        return new CreateCommentRequest(1L, 1L, "Reply Comment Content");
    }

    private CreateCommentRequest createRequest(Long postId, Long parentCommentId, String content) {
        return new CreateCommentRequest(postId, parentCommentId, content);
    }

    private CreateCommentResponse createSuccessResponseUnderPost() {
        return new CreateCommentResponse(1L, "Test Comment Content", 1L, 1L, null,
                java.time.LocalDateTime.now());
    }

    private CreateCommentResponse createSuccessResponseUnderComment() {
        return new CreateCommentResponse(2L, "Reply Comment Content", 1L, 1L, 1L,
                java.time.LocalDateTime.now());
    }
}
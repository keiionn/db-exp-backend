package com.dbexp.db_experiment.service.comment;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.comment.GetCommentByIdRequest;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdResponse;
import com.dbexp.db_experiment.entity.Comment;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.testutil.CommentTestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("Comment Service - Get Comment By ID Tests")
class CommentServiceGetByIdTest extends BaseCommentServiceTest {

    @Nested
    @DisplayName("Successful Retrieval")
    class SuccessTests {

        @Test
        @DisplayName("Should retrieve comment successfully with valid ID")
        void getById_Success() {
            // Arrange
            Long commentId = 1L;
            GetCommentByIdRequest request = new GetCommentByIdRequest(commentId);
            Comment comment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(1L)
                    .withPostId(1L)
                    .withCommentContent("Test Comment Content")
                    .withParentCommentId(null)
                    .build();

            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

            // Act
            GetCommentByIdResponse response = commentService.getById(request);

            // Assert
            assertNotNull(response);
            assertEquals(commentId, response.getCommentId());
            assertEquals(1L, response.getUserId());
            assertEquals(1L, response.getPostId());
            assertEquals("Test Comment Content", response.getContent());
            assertEquals(null, response.getParentCommentId());
            assertNotNull(response.getCreatedAt());

            verify(commentRepository).findById(commentId);
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should throw exception when comment ID does not exist")
        void getById_NotFound_ThrowsException() {
            // Arrange
            Long commentId = 999L;
            GetCommentByIdRequest request = new GetCommentByIdRequest(commentId);

            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                commentService.getById(request);
            });

            assertEquals("Comment not found", exception.getMessage());

            verify(commentRepository).findById(commentId);
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when comment ID is null")
        void getById_NullCommentId_ThrowsException() {
            // Arrange
            GetCommentByIdRequest request = new GetCommentByIdRequest(null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.getById(request);
            });

            verifyNoInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Server Errors")
    class ServerErrorsTests {

        @Test
        @DisplayName("Should throw exception when repository findById fails")
        void getById_RepositoryFails_ThrowsException() {
            // Arrange
            Long commentId = 1L;
            GetCommentByIdRequest request = new GetCommentByIdRequest(commentId);

            when(commentRepository.findById(commentId)).thenThrow(new RuntimeException("Database error"));

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                commentService.getById(request);
            });

            verify(commentRepository).findById(commentId);
        }
    }
}
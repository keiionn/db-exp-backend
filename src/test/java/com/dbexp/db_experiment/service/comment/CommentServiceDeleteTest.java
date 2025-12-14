package com.dbexp.db_experiment.service.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.comment.DeleteCommentResponse;
import com.dbexp.db_experiment.entity.Comment;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.testutil.CommentTestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Comment Service - Delete Comment Tests")
class CommentServiceDeleteTest extends BaseCommentServiceTest {

    @Nested
    @DisplayName("Successful Deletion")
    class SuccessTests {

        @Test
        @DisplayName("Should delete comment successfully with valid data")
        void delete_Success() {
            // Arrange
            Long commentId = 1L;
            Long userId = 1L;

            Comment existingComment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(userId)
                    .withCommentContent("Test Comment Content")
                    .build();

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindById(commentId, existingComment);
            mockCommentRepositoryDelete(commentId, 1);

            // Act
            DeleteCommentResponse response = commentService.delete(session, commentId);

            // Assert
            assertNotNull(response);
            assertEquals(commentId, response.getCommentId());
            assertNotNull(response.getDeletedAt());
            assertEquals("Comment deleted successfully", response.getMessage());

            verify(commentRepository).findById(commentId);
            verify(commentRepository).deleteByCommentId(commentId);
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should throw exception when comment ID does not exist")
        void delete_NotFound_ThrowsException() {
            // Arrange
            Long commentId = 999L;
            Long userId = 1L;

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindByIdNotFound(commentId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                commentService.delete(session, commentId);
            });

            assertEquals("Comment not found", exception.getMessage());

            verify(commentRepository).findById(commentId);
            verify(commentRepository, never()).deleteByCommentId(anyLong());
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Not Owner")
    class NotOwnerTests {

        @Test
        @DisplayName("Should throw exception when user is not the owner of the comment")
        void delete_NotOwner_ThrowsException() {
            // Arrange
            Long commentId = 1L;
            Long userId = 1L;
            Long ownerId = 2L;

            Comment existingComment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(ownerId)
                    .build();

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindById(commentId, existingComment);

            // Act & Assert
            ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
                commentService.delete(session, commentId);
            });

            assertEquals("Cannot delete another user's comment", exception.getMessage());

            verify(commentRepository).findById(commentId);
            verify(commentRepository, never()).deleteByCommentId(anyLong());
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Delete Failure")
    class DeleteFailureTests {

        @Test
        @DisplayName("Should throw exception when delete operation fails")
        void delete_DeleteFails_ThrowsException() {
            // Arrange
            Long commentId = 1L;
            Long userId = 1L;

            Comment existingComment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(userId)
                    .build();

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindById(commentId, existingComment);
            mockCommentRepositoryDelete(commentId, 0);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                commentService.delete(session, commentId);
            });

            assertEquals("Failed to delete comment", exception.getMessage());

            verify(commentRepository).findById(commentId);
            verify(commentRepository).deleteByCommentId(commentId);
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when not logged in")
        void delete_NotLoggedIn_ThrowsException() {
            // Arrange
            Long commentId = 1L;

            mockUnauthenticatedUser();

            // Act & Assert
            assertThrows(UnauthorizedException.class, () -> {
                commentService.delete(session, commentId);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when comment ID is null")
        void delete_NullCommentId_ThrowsException() {
            // Arrange
            Long userId = 1L;

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.delete(session, null);
            });

            verifyNoInteractions(commentRepository);
        }
    }
}
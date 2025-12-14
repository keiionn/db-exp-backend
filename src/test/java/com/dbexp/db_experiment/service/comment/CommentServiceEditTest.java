package com.dbexp.db_experiment.service.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.comment.EditCommentRequest;
import com.dbexp.db_experiment.dto.comment.EditCommentResponse;
import com.dbexp.db_experiment.entity.Comment;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.testutil.CommentTestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Comment Service - Edit Comment Tests")
class CommentServiceEditTest extends BaseCommentServiceTest {

    @Nested
    @DisplayName("Successful Edit")
    class SuccessTests {

        @Test
        @DisplayName("Should edit comment successfully with valid data")
        void edit_Success() {
            // Arrange
            Long commentId = 1L;
            Long userId = 1L;
            String oldContent = "Old Content";
            String newContent = "New Content";

            EditCommentRequest request = new EditCommentRequest(newContent);

            Comment existingComment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(userId)
                    .withCommentContent(oldContent)
                    .build();

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindById(commentId, existingComment);
            mockCommentRepositoryUpdate(commentId, newContent, 1);

            // Act
            EditCommentResponse response = commentService.edit(session, commentId, request);

            // Assert
            assertNotNull(response);
            assertEquals(commentId, response.getCommentId());
            assertEquals(oldContent, response.getOldContent());
            assertEquals(newContent, response.getNewContent());
            assertNotNull(response.getEditedAt());

            verify(commentRepository).findById(commentId);
            verify(commentRepository).updateComment(commentId, newContent);
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should throw exception when comment ID does not exist")
        void edit_NotFound_ThrowsException() {
            // Arrange
            Long commentId = 999L;
            Long userId = 1L;
            EditCommentRequest request = new EditCommentRequest("New Content");

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindByIdNotFound(commentId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                commentService.edit(session, commentId, request);
            });

            assertEquals("Comment not found", exception.getMessage());

            verify(commentRepository).findById(commentId);
            verify(commentRepository, never()).updateComment(anyLong(), any());
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Not Owner")
    class NotOwnerTests {

        @Test
        @DisplayName("Should throw exception when user is not the owner of the comment")
        void edit_NotOwner_ThrowsException() {
            // Arrange
            Long commentId = 1L;
            Long userId = 1L;
            Long ownerId = 2L;
            EditCommentRequest request = new EditCommentRequest("New Content");

            Comment existingComment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(ownerId)
                    .build();

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindById(commentId, existingComment);

            // Act & Assert
            ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
                commentService.edit(session, commentId, request);
            });

            assertEquals("Cannot edit another user's comment", exception.getMessage());

            verify(commentRepository).findById(commentId);
            verify(commentRepository, never()).updateComment(anyLong(), any());
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Update Failure")
    class UpdateFailureTests {

        @Test
        @DisplayName("Should throw exception when update operation fails")
        void edit_UpdateFails_ThrowsException() {
            // Arrange
            Long commentId = 1L;
            Long userId = 1L;
            EditCommentRequest request = new EditCommentRequest("New Content");

            Comment existingComment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(userId)
                    .build();

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindById(commentId, existingComment);
            mockCommentRepositoryUpdate(commentId, "New Content", 0);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                commentService.edit(session, commentId, request);
            });

            assertEquals("Failed to update comment", exception.getMessage());

            verify(commentRepository).findById(commentId);
            verify(commentRepository).updateComment(commentId, "New Content");
            verifyNoMoreInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when not logged in")
        void edit_NotLoggedIn_ThrowsException() {
            // Arrange
            Long commentId = 1L;
            EditCommentRequest request = new EditCommentRequest("New Content");

            mockUnauthenticatedUser();

            // Act & Assert
            assertThrows(UnauthorizedException.class, () -> {
                commentService.edit(session, commentId, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when comment ID is null")
        void edit_NullCommentId_ThrowsException() {
            // Arrange
            Long userId = 1L;
            EditCommentRequest request = new EditCommentRequest("New Content");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.edit(session, null, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when content is null")
        void edit_NullContent_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long commentId = 1L;
            EditCommentRequest request = new EditCommentRequest(null);

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.edit(session, commentId, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when content is empty")
        void edit_EmptyContent_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long commentId = 1L;
            EditCommentRequest request = new EditCommentRequest("");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.edit(session, commentId, request);
            });

            verifyNoInteractions(commentRepository);
        }
    }
}
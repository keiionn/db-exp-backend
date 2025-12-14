package com.dbexp.db_experiment.service.comment;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.comment.CreateCommentRequest;
import com.dbexp.db_experiment.dto.comment.CreateCommentResponse;
import com.dbexp.db_experiment.entity.Comment;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.testutil.CommentTestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("Comment Service - Create Comment Tests")
class CommentServiceCreateTest extends BaseCommentServiceTest {

    @Nested
    @DisplayName("Successful Creation")
    class SuccessTests {

        @Test
        @DisplayName("Should create comment under post successfully with valid data")
        void createUnderPost_Success() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            CreateCommentRequest request = createRequest(postId, null, "Test Comment Content");
            Comment savedComment = CommentTestBuilder.aComment()
                    .withCommentId(1L)
                    .withUserId(userId)
                    .withPostId(request.getPostId())
                    .withCommentContent(request.getContent())
                    .withParentCommentId(null)
                    .build();

            // Create a mock post
            Post mockPost = new Post(userId, 1L,
                    "Test Post", "Test Content");
            mockPost.setPostId(postId);

            mockAuthenticatedUser(userId);
            mockPostRepositoryFindById(postId, mockPost);
            mockCommentRepositorySave(savedComment);

            // Act
            CreateCommentResponse response = commentService.createUnderPost(session, request);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.getCommentId());
            assertEquals(userId, response.getUserId());
            assertEquals(request.getPostId(), response.getPostId());
            assertEquals(request.getContent(), response.getContent());
            assertEquals(null, response.getParentCommentId());
            assertNotNull(response.getCreatedAt());

            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        @DisplayName("Should create comment under comment successfully with valid data")
        void createUnderComment_Success() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            Long commentId = 2L;
            CreateCommentRequest request = createRequest(postId, commentId, "Test Reply Content");
            Comment parentComment = CommentTestBuilder.aComment()
                    .withCommentId(request.getParentCommentId())
                    .build();
            Comment savedComment = CommentTestBuilder.aComment()
                    .withCommentId(commentId)
                    .withUserId(userId)
                    .withPostId(request.getPostId())
                    .withCommentContent(request.getContent())
                    .withParentCommentId(request.getParentCommentId())
                    .build();

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindById(commentId, savedComment);
            mockCommentRepositorySave(savedComment);

            // Act
            CreateCommentResponse response = commentService.createUnderComment(session, request);

            // Assert
            assertNotNull(response);
            assertEquals(2L, response.getCommentId());
            assertEquals(userId, response.getUserId());
            assertEquals(request.getPostId(), response.getPostId());
            assertEquals(request.getContent(), response.getContent());
            assertEquals(request.getParentCommentId(), response.getParentCommentId());
            assertNotNull(response.getCreatedAt());

            verify(commentRepository).findById(request.getParentCommentId());
            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when not logged in for createUnderPost")
        void createUnderPost_NotLoggedIn_ThrowsException() {
            // Arrange
            Long postId = 1L;
            CreateCommentRequest request = createRequest(postId, null, "Test Content");

            mockUnauthenticatedUser();

            // Act & Assert
            assertThrows(UnauthorizedException.class, () -> {
                commentService.createUnderPost(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when post ID is null for createUnderPost")
        void createUnderPost_NullPostId_ThrowsException() {
            // Arrange
            Long userId = 1L;
            CreateCommentRequest request = createRequest(null, null, "Test Content");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderPost(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when content is null for createUnderPost")
        void createUnderPost_NullContent_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            CreateCommentRequest request = createRequest(postId, null, null);

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderPost(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when content is empty for createUnderPost")
        void createUnderPost_EmptyContent_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            CreateCommentRequest request = createRequest(postId, null, "");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderPost(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when parent comment ID is provided for createUnderPost")
        void createUnderPost_ParentCommentIdProvided_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            Long parentCommentId = 2L;
            CreateCommentRequest request = createRequest(postId, parentCommentId, "Test Content");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderPost(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when not logged in for createUnderComment")
        void createUnderComment_NotLoggedIn_ThrowsException() {
            // Arrange
            Long postId = 1L;
            Long parentCommentId = 2L;
            CreateCommentRequest request = createRequest(postId, parentCommentId, "Test Content");

            mockUnauthenticatedUser();

            // Act & Assert
            assertThrows(UnauthorizedException.class, () -> {
                commentService.createUnderComment(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when post ID is null for createUnderComment")
        void createUnderComment_NullPostId_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long parentCommentId = 2L;
            CreateCommentRequest request = createRequest(null, parentCommentId, "Test Content");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderComment(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when content is null for createUnderComment")
        void createUnderComment_NullContent_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            Long parentCommentId = 2L;
            CreateCommentRequest request = createRequest(postId, parentCommentId, null);

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderComment(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when content is empty for createUnderComment")
        void createUnderComment_EmptyContent_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            Long parentCommentId = 2L;
            CreateCommentRequest request = createRequest(postId, parentCommentId, "");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderComment(session, request);
            });

            verifyNoInteractions(commentRepository);
        }

        @Test
        @DisplayName("Should throw exception when parent comment ID is null for createUnderComment")
        void createUnderComment_NullParentCommentId_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            CreateCommentRequest request = createRequest(postId, null, "Test Content");

            mockAuthenticatedUser(userId);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                commentService.createUnderComment(session, request);
            });

            verifyNoInteractions(commentRepository);
        }
    }

    @Nested
    @DisplayName("Business Logic")
    class BusinessLogicTests {

        @Test
        @DisplayName("Should throw exception when parent comment does not exist for createUnderComment")
        void createUnderComment_ParentCommentNotFound_ThrowsException() {
            // Arrange
            Long userId = 1L;
            Long postId = 1L;
            Long parentCommentId = 2L;
            CreateCommentRequest request = createRequest(postId, parentCommentId, "Test Reply Content");

            mockAuthenticatedUser(userId);
            mockCommentRepositoryFindByIdNotFound(parentCommentId);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                commentService.createUnderComment(session, request);
            });

            verify(commentRepository).findById(request.getParentCommentId());
            verify(commentRepository, org.mockito.Mockito.never()).save(any(Comment.class));
        }
    }

    // Helper methods
    private CreateCommentRequest createRequest(Long postId, Long parentCommentId, String content) {
        return new CreateCommentRequest(postId, parentCommentId, content);
    }
}
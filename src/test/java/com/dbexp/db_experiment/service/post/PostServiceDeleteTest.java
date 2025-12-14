package com.dbexp.db_experiment.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.post.DeletePostResponse;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Post Service - Delete Post Tests")
class PostServiceDeleteTest extends BasePostServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        mockAuthenticatedUser(1L);
    }

    @Nested
    @DisplayName("Successful Deletion")
    class SuccessTests {

        @Test
        @DisplayName("Should delete post successfully with valid ID")
        void deletePost_Success() {
            // Arrange
            Long postId = 1L;

            Post existingPost = createMockPost(postId, 1L, 1L, "Test Post Title", "Test Post Content");

            mockPostRepositoryFindById(postId, existingPost);
            mockPostRepositoryDelete(postId, 1);

            // Act
            DeletePostResponse response = postService.deletePost(session, postId);

            // Assert
            assertNotNull(response);
            assertEquals(postId, response.getPostId());
            assertNotNull(response.getDeletedAt());
            assertEquals("Post deleted successfully", response.getMessage());

            verify(postRepository).findById(postId);
            verify(postRepository).deleteByPostId(postId);
            verifyNoMoreInteractions(postRepository);
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should throw exception when post ID does not exist")
        void deletePost_NotFound_ThrowsException() {
            // Arrange
            Long postId = 999L;

            mockPostRepositoryFindByIdNotFound(postId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                postService.deletePost(session, postId);
            });

            assertEquals("Post not found", exception.getMessage());

            verify(postRepository).findById(postId);
            verify(postRepository, never()).deleteByPostId(anyLong());
            verifyNoMoreInteractions(postRepository);
        }
    }

    @Nested
    @DisplayName("Delete Failure")
    class DeleteFailureTests {

        @Test
        @DisplayName("Should throw exception when delete operation fails")
        void deletePost_DeleteFails_ThrowsException() {
            // Arrange
            Long postId = 1L;

            Post existingPost = createMockPost(postId, 1L, 1L, "Test Post Title", "Test Post Content");

            mockPostRepositoryFindById(postId, existingPost);
            mockPostRepositoryDelete(postId, 0);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                postService.deletePost(session, postId);
            });

            assertEquals("Failed to delete post", exception.getMessage());

            verify(postRepository).findById(postId);
            verify(postRepository).deleteByPostId(postId);
            verifyNoMoreInteractions(postRepository);
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when post ID is null")
        void deletePost_NullPostId_ThrowsException() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.deletePost(session, null);
            });

            verifyNoInteractions(postRepository);
        }
    }
}
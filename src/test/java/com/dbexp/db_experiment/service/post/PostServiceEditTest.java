package com.dbexp.db_experiment.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.post.EditPostRequest;
import com.dbexp.db_experiment.dto.post.EditPostResponse;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@DisplayName("Post Service - Edit Post Tests")
class PostServiceEditTest extends BasePostServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        mockAuthenticatedUser(1L);
    }

    @Nested
    @DisplayName("Successful Edit")
    class SuccessTests {

        @Test
        @DisplayName("Should edit post successfully with valid data")
        void editPost_Success() {
            // Arrange
            Long postId = 1L;
            String oldTitle = "Old Title";
            String oldContent = "Old Content";
            String newTitle = "New Title";
            String newContent = "New Content";

            EditPostRequest request = new EditPostRequest(newTitle, newContent);

            Post existingPost = createMockPost(postId, 1L, 1L, oldTitle, oldContent);

            mockPostRepositoryFindById(postId, existingPost);
            mockPostRepositoryUpdate(postId, newTitle, newContent, 1);

            // Act
            EditPostResponse response = postService.editPost(session, postId, request);

            // Assert
            assertNotNull(response);
            assertEquals(postId, response.getPostId());
            assertEquals(oldTitle, response.getOldPostTitle());
            assertEquals(newTitle, response.getNewPostTitle());
            assertEquals(oldContent, response.getOldPostContent());
            assertEquals(newContent, response.getNewPostContent());
            assertNotNull(response.getUpdatedAt());

            verify(postRepository).findById(postId);
            verify(postRepository).updatePost(postId, newTitle, newContent);
            verifyNoMoreInteractions(postRepository);
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should throw exception when post ID does not exist")
        void editPost_NotFound_ThrowsException() {
            // Arrange
            Long postId = 999L;
            EditPostRequest request = new EditPostRequest("New Title", "New Content");

            mockPostRepositoryFindByIdNotFound(postId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                postService.editPost(session, postId, request);
            });

            assertEquals("Post not found", exception.getMessage());

            verify(postRepository).findById(postId);
            verify(postRepository, never()).updatePost(anyLong(), any(), any());
            verifyNoMoreInteractions(postRepository);
        }
    }

    @Nested
    @DisplayName("Update Failure")
    class UpdateFailureTests {

        @Test
        @DisplayName("Should throw exception when update operation fails")
        void editPost_UpdateFails_ThrowsException() {
            // Arrange
            Long postId = 1L;
            EditPostRequest request = new EditPostRequest("New Title", "New Content");

            Post existingPost = createMockPost(postId, 1L, 1L, "Old Title", "Old Content");

            mockPostRepositoryFindById(postId, existingPost);
            mockPostRepositoryUpdate(postId, "New Title", "New Content", 0);

            // Act & Assert
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                postService.editPost(session, postId, request);
            });

            assertEquals("Failed to update post", exception.getMessage());

            verify(postRepository).findById(postId);
            verify(postRepository).updatePost(postId, "New Title", "New Content");
            verifyNoMoreInteractions(postRepository);
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when post title is null")
        void editPost_NullPostTitle_ThrowsException() {
            // Arrange
            Long postId = 1L;
            EditPostRequest request = new EditPostRequest(null, "New Content");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.editPost(session, postId, request);
            });

            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("Should throw exception when post title is empty")
        void editPost_EmptyPostTitle_ThrowsException() {
            // Arrange
            Long postId = 1L;
            EditPostRequest request = new EditPostRequest("", "New Content");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.editPost(session, postId, request);
            });

            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("Should throw exception when post content is null")
        void editPost_NullPostContent_ThrowsException() {
            // Arrange
            Long postId = 1L;
            EditPostRequest request = new EditPostRequest("New Title", null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.editPost(session, postId, request);
            });

            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("Should throw exception when post content is empty")
        void editPost_EmptyPostContent_ThrowsException() {
            // Arrange
            Long postId = 1L;
            EditPostRequest request = new EditPostRequest("New Title", "");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.editPost(session, postId, request);
            });

            verifyNoInteractions(postRepository);
        }
    }
}
package com.dbexp.db_experiment.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.post.GetPostByIdRequest;
import com.dbexp.db_experiment.dto.post.GetPostByIdResponse;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("Post Service - Get Post By ID Tests")
class PostServiceGetByIdTest extends BasePostServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
    }

    @Nested
    @DisplayName("Successful Retrieval")
    class SuccessTests {

        @Test
        @DisplayName("Should retrieve post successfully with valid ID")
        void getPostById_Success() {
            // Arrange
            Long postId = 1L;
            GetPostByIdRequest request = new GetPostByIdRequest(postId);

            Post post = createMockPost(postId, 1L, 1L, "Test Post Title", "Test Post Content");

            mockPostRepositoryFindById(postId, post);

            // Act
            GetPostByIdResponse response = postService.getPostById(request);

            // Assert
            assertNotNull(response);
            assertEquals(postId, response.getPostId());
            assertEquals(1L, response.getUserId());
            assertEquals(1L, response.getCommunityId());
            assertEquals("Test Post Title", response.getPostTitle());
            assertEquals("Test Post Content", response.getPostContent());
            assertNotNull(response.getCreatedAt());

            verify(postRepository).findById(postId);
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFoundTests {

        @Test
        @DisplayName("Should throw exception when post ID does not exist")
        void getPostById_NotFound_ThrowsException() {
            // Arrange
            Long postId = 999L;
            GetPostByIdRequest request = new GetPostByIdRequest(postId);

            mockPostRepositoryFindByIdNotFound(postId);

            // Act & Assert
            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                postService.getPostById(request);
            });

            assertEquals("Post not found", exception.getMessage());

            verify(postRepository).findById(postId);
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when post ID is null")
        void getPostById_NullPostId_ThrowsException() {
            // Arrange
            GetPostByIdRequest request = new GetPostByIdRequest(null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.getPostById(request);
            });

            verifyNoInteractions(postRepository);
        }
    }
}
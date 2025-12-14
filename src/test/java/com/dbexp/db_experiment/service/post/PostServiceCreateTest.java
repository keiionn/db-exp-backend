package com.dbexp.db_experiment.service.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.dbexp.db_experiment.dto.post.CreatePostRequest;
import com.dbexp.db_experiment.dto.post.CreatePostResponse;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.testutil.PostTestBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@DisplayName("Post Service - Create Post Tests")
class PostServiceCreateTest extends BasePostServiceTest {

    @BeforeEach
    void setUp() {
        super.setUp();
        mockAuthenticatedUser(1L);
    }

    @Nested
    @DisplayName("Successful Creation")
    class SuccessTests {

        @Test
        @DisplayName("Should create post successfully with valid data")
        void createPost_Success() {
            // Arrange
            CreatePostRequest request = createValidRequest();
            Post savedPost = PostTestBuilder.aPost()
                    .withPostId(1L)
                    .withUserId(1L)
                    .withCommunityId(request.getCommunityId())
                    .withPostTitle(request.getPostTitle())
                    .withPostContent(request.getPostContent())
                    .build();

            mockPostRepositorySave(savedPost);
            mockCommunityRepositoryFindById(request.getCommunityId());

            // Act
            CreatePostResponse response = postService.createPost(session, request);

            // Assert
            assertNotNull(response);
            assertEquals(1L, response.getPostId());
            assertEquals(1L, response.getUserId());
            assertEquals(request.getCommunityId(), response.getCommunityId());
            assertEquals(request.getPostTitle(), response.getPostTitle());
            assertEquals(request.getPostContent(), response.getPostContent());
            assertNotNull(response.getCreatedAt());

            verify(postRepository).save(any(Post.class));
        }
    }

    @Nested
    @DisplayName("Validation")
    class ValidationTests {

        @Test
        @DisplayName("Should throw exception when community ID is null")
        void createPost_NullCommunityId_ThrowsException() {
            // Arrange
            CreatePostRequest request = createRequest(null, "Test Title", "Test Content");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.createPost(session, request);
            });

            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("Should throw exception when post title is null")
        void createPost_NullPostTitle_ThrowsException() {
            // Arrange
            CreatePostRequest request = createRequest(1L, null, "Test Content");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.createPost(session, request);
            });

            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("Should throw exception when post title is empty")
        void createPost_EmptyPostTitle_ThrowsException() {
            // Arrange
            CreatePostRequest request = createRequest(1L, "", "Test Content");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.createPost(session, request);
            });

            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("Should throw exception when post content is null")
        void createPost_NullPostContent_ThrowsException() {
            // Arrange
            CreatePostRequest request = createRequest(1L, "Test Title", null);

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.createPost(session, request);
            });

            verifyNoInteractions(postRepository);
        }

        @Test
        @DisplayName("Should throw exception when post content is empty")
        void createPost_EmptyPostContent_ThrowsException() {
            // Arrange
            CreatePostRequest request = createRequest(1L, "Test Title", "");

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                postService.createPost(session, request);
            });

            verifyNoInteractions(postRepository);
        }
    }

    // Helper methods
    private CreatePostRequest createValidRequest() {
        return new CreatePostRequest(1L, "Test Post Title", "Test Post Content");
    }

    private CreatePostRequest createRequest(Long communityId, String postTitle, String postContent) {
        return new CreatePostRequest(communityId, postTitle, postContent);
    }
}
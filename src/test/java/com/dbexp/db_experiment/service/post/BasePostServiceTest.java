package com.dbexp.db_experiment.service.post;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.repository.CommunityRepository;
import com.dbexp.db_experiment.repository.PostRepository;
import com.dbexp.db_experiment.service.AuthService;
import com.dbexp.db_experiment.service.PostServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class BasePostServiceTest {

    @Mock
    protected PostRepository postRepository;

    @Mock
    protected CommunityRepository communityRepository;

    @Mock
    protected AuthService authService;

    protected PostServiceImpl postService;

    @Mock
    protected HttpSession session;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository, authService, communityRepository);
        session = mock(HttpSession.class);
    }

    protected Post createMockPost(Long postId, Long userId, Long communityId, String postTitle, String postContent) {
        Post post = new Post(userId, communityId, postTitle, postContent);
        post.setPostId(postId);
        post.setCreatedAt(java.time.LocalDateTime.now());
        return post;
    }

    protected void mockPostRepositoryFindById(Long postId, Post post) {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    }

    protected void mockPostRepositoryFindByIdNotFound(Long postId) {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
    }

    protected void mockPostRepositorySave(Post post) {
        when(postRepository.save(any(Post.class))).thenReturn(post);
    }

    protected void mockPostRepositoryUpdate(Long postId, String postTitle, String postContent, int affectedRows) {
        when(postRepository.updatePost(postId, postTitle, postContent)).thenReturn(affectedRows);
    }

    protected void mockPostRepositoryDelete(Long postId, int affectedRows) {
        when(postRepository.deleteByPostId(postId)).thenReturn(affectedRows);
    }

    protected void mockAuthenticatedUser(Long userId) {
        CurrentUserResponse response = new CurrentUserResponse(true, userId, "testuser", "test@example.com");
        when(authService.getCurrentUser(session)).thenReturn(response);
    }

    protected void mockCommunityRepositoryFindById(Long communityId) {
        when(communityRepository.findById(communityId))
                .thenReturn(Optional
                        .of(new com.dbexp.db_experiment.entity.Community("Test Community", "Test Description")));
    }
}

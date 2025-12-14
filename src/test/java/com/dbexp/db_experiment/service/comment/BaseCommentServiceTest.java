package com.dbexp.db_experiment.service.comment;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.entity.Comment;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.repository.CommentRepository;
import com.dbexp.db_experiment.repository.PostRepository;
import com.dbexp.db_experiment.service.AuthService;
import com.dbexp.db_experiment.service.CommentServiceImpl;
import com.dbexp.db_experiment.testutil.CommentTestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public abstract class BaseCommentServiceTest {

    @Mock
    protected CommentRepository commentRepository;

    @Mock
    protected PostRepository postRepository;

    protected CommentServiceImpl commentService;

    @Mock
    protected AuthService authService;

    @Mock
    protected HttpSession session;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, postRepository, authService);
        session = mock(HttpSession.class);
    }

    protected Comment createMockComment(Long commentId, Long userId, Long postId, String content,
            Long parentCommentId) {
        Comment comment = CommentTestBuilder.aComment()
                .withCommentId(commentId)
                .withUserId(userId)
                .withPostId(postId)
                .withCommentContent(content)
                .withParentCommentId(parentCommentId)
                .build();
        return comment;
    }

    protected void mockCommentRepositoryFindById(Long commentId, Comment comment) {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
    }

    protected void mockCommentRepositoryFindByIdNotFound(Long commentId) {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
    }

    protected void mockCommentRepositorySave(Comment comment) {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
    }

    protected void mockCommentRepositoryUpdate(Long commentId, String content, int affectedRows) {
        when(commentRepository.updateComment(commentId, content)).thenReturn(affectedRows);
    }

    protected void mockCommentRepositoryDelete(Long commentId, int affectedRows) {
        when(commentRepository.deleteByCommentId(commentId)).thenReturn(affectedRows);
    }

    protected void mockAuthenticatedUser(Long userId) {
        CurrentUserResponse response = new CurrentUserResponse(true, userId, "testuser", "test@example.com");
        when(authService.getCurrentUser(session)).thenReturn(response);
    }

    protected void mockUnauthenticatedUser() {
        CurrentUserResponse response = new CurrentUserResponse(false, null, null, null);
        when(authService.getCurrentUser(session)).thenReturn(response);
    }

    protected void mockPostRepositoryFindById(Long postId, Post post) {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
    }

    protected void mockPostRepositoryFindByIdNotFound(Long postId) {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
    }
}

package com.dbexp.db_experiment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.comment.CreateCommentRequest;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdResponse;
import com.dbexp.db_experiment.dto.comment.CreateCommentResponse;
import com.dbexp.db_experiment.dto.comment.DeleteCommentResponse;
import com.dbexp.db_experiment.dto.comment.EditCommentRequest;
import com.dbexp.db_experiment.dto.comment.EditCommentResponse;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdRequest;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdResponse;
import com.dbexp.db_experiment.entity.Comment;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.repository.CommentRepository;
import com.dbexp.db_experiment.repository.PostRepository;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final AuthService authService;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
            AuthService authService) {
        this.authService = authService;
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public CreateCommentResponse createUnderPost(HttpSession session, CreateCommentRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUser = authService.getCurrentUser(session);
        if (!currentUser.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        Long userId = currentUser.userId();
        if (request.getPostId() == null) {
            throw new IllegalArgumentException("Post ID is required");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }
        if (request.getParentCommentId() != null) {
            throw new IllegalArgumentException("Parent comment ID must be null for post comments");
        }

        // Validate post existence
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Create new Comment entity
        Comment comment = new Comment(
                userId,
                request.getPostId(),
                request.getContent());
        comment.setParentCommentId(null);
        comment.setCreatedAt(LocalDateTime.now());

        // Save comment to database
        Comment savedComment = commentRepository.save(comment);

        // Return response DTO
        return new CreateCommentResponse(
                savedComment.getCommentId(),
                savedComment.getCommentContent(),
                savedComment.getUserId(),
                savedComment.getPostId(),
                savedComment.getParentCommentId(),
                savedComment.getCreatedAt());
    }

    @Override
    @Transactional
    public CreateCommentResponse createUnderComment(HttpSession session, CreateCommentRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUser = authService.getCurrentUser(session);
        if (!currentUser.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        Long userId = currentUser.userId();

        if (request.getPostId() == null) {
            throw new IllegalArgumentException("Post ID is required");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }
        if (request.getParentCommentId() == null) {
            throw new IllegalArgumentException("Parent comment ID is required for replies");
        }

        // Validate parent comment exists
        Comment parentComment = commentRepository.findById(request.getParentCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));

        // Validate parent comment belongs to the same post
        if (!parentComment.getPostId().equals(request.getPostId())) {
            throw new IllegalArgumentException("Parent comment does not belong to the specified post");
        }

        // Create new Comment entity
        Comment comment = new Comment(
                userId,
                request.getPostId(),
                request.getContent());
        comment.setParentCommentId(request.getParentCommentId());
        comment.setCreatedAt(LocalDateTime.now());

        // Save comment to database
        Comment savedComment = commentRepository.save(comment);

        // Return response DTO
        return new CreateCommentResponse(
                savedComment.getCommentId(),
                savedComment.getCommentContent(),
                savedComment.getUserId(),
                savedComment.getPostId(),
                savedComment.getParentCommentId(),
                savedComment.getCreatedAt());
    }

    @Override
    public GetCommentByIdResponse getById(GetCommentByIdRequest request) {
        // Validate input parameters
        if (request.getCommentId() == null) {
            throw new IllegalArgumentException("Comment ID must not be null.");
        }

        // Fetch comment from database and validate existence
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Comment not found"));

        // Return response DTO
        return new GetCommentByIdResponse(
                comment.getCommentId(),
                comment.getCommentContent(),
                comment.getUserId(),
                comment.getPostId(),
                comment.getParentCommentId(),
                comment.getCreatedAt());
    }
    
    @Override
    public List<GetCommentByIdResponse> getCommentsByPostId(Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID is required");
        }
        
        List<Comment> comments = commentRepository.findByPostId(postId);
        List<GetCommentByIdResponse> responses = new ArrayList<>();
        
        for (Comment comment : comments) {
            responses.add(new GetCommentByIdResponse(
                comment.getCommentId(),
                comment.getCommentContent(),
                comment.getUserId(),
                comment.getPostId(),
                comment.getParentCommentId(),
                comment.getCreatedAt()
            ));
        }
        
        return responses;
    }

    @Override
    @Transactional
    public EditCommentResponse edit(HttpSession session, Long commentId, EditCommentRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUser = authService.getCurrentUser(session);
        if (!currentUser.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        Long userId = currentUser.userId();

        // Validate request
        if (commentId == null) {
            throw new IllegalArgumentException("Comment ID must not be null.");
        }
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Comment content is required");
        }

        // Validate comment exists
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Ensure user can only edit their own comments
        if (!comment.getUserId().equals(currentUser.userId())) {
            throw new ForbiddenException("Cannot edit another user's comment");
        }

        // Validate user can only edit their own comments
        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException("User can only edit their own comments");
        }

        // Store old content
        String oldContent = comment.getCommentContent();

        // Update comment using raw SQL query
        int rowsUpdated = commentRepository.updateComment(commentId, request.getContent());

        if (rowsUpdated == 0) {
            throw new IllegalStateException("Failed to update comment");
        }

        // Return response DTO
        return new EditCommentResponse(
                commentId,
                oldContent,
                request.getContent(),
                LocalDateTime.now());
    }

    @Override
    @Transactional
    public DeleteCommentResponse delete(HttpSession session, Long commentId) {
        // Validate session and get current user
        CurrentUserResponse currentUser = authService.getCurrentUser(session);
        if (!currentUser.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        Long userId = currentUser.userId();

        // Validate request
        if (commentId == null) {
            throw new IllegalArgumentException("Comment ID is required");
        }

        // Validate comment exists
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Ensure user can only delete their own comments
        if (!comment.getUserId().equals(currentUser.userId())) {
            throw new ForbiddenException("Cannot delete another user's comment");
        }

        // Validate user can only delete their own comments
        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException("User can only delete their own comments");
        }

        // Delete comment using raw SQL query
        int rowsDeleted = commentRepository.deleteByCommentId(commentId);

        if (rowsDeleted == 0) {
            throw new IllegalStateException("Failed to delete comment");
        }

        // Return response DTO
        return new DeleteCommentResponse(
                commentId,
                LocalDateTime.now(),
                "Comment deleted successfully");
    }
}
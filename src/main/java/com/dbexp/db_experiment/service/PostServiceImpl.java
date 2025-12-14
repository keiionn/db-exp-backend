package com.dbexp.db_experiment.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.post.CreatePostRequest;
import com.dbexp.db_experiment.dto.post.CreatePostResponse;
import com.dbexp.db_experiment.dto.post.DeletePostResponse;
import com.dbexp.db_experiment.dto.post.EditPostRequest;
import com.dbexp.db_experiment.dto.post.EditPostResponse;
import com.dbexp.db_experiment.dto.post.GetPostByIdRequest;
import com.dbexp.db_experiment.dto.post.GetPostByIdResponse;
import com.dbexp.db_experiment.entity.Community;
import com.dbexp.db_experiment.entity.Post;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.repository.CommunityRepository;
import com.dbexp.db_experiment.repository.PostRepository;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final AuthService authService;
    private final CommunityRepository communityRepository;

    public PostServiceImpl(PostRepository postRepository, AuthService authService,
            CommunityRepository communityRepository) {
        this.postRepository = postRepository;
        this.authService = authService;
        this.communityRepository = communityRepository;
    }

    @Override
    @Transactional
    public CreatePostResponse createPost(HttpSession session, CreatePostRequest request) {
        // Get current user ID from session
        CurrentUserResponse currentUser = authService.getCurrentUser(session);
        if (!currentUser.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        Long userId = currentUser.userId();

        // Validate input parameters
        if (request.getCommunityId() == null) {
            throw new IllegalArgumentException("Community ID is required");
        }
        if (request.getPostTitle() == null || request.getPostTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title is required");
        }
        if (request.getPostContent() == null || request.getPostContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content is required");
        }

        // Validate community exists
        Community community = communityRepository.findById(request.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        // Create new Post entity
        Post post = new Post(
                userId,
                request.getCommunityId(),
                request.getPostTitle(),
                request.getPostContent());
        post.setCreatedAt(LocalDateTime.now());

        // Save post to database
        Post savedPost = postRepository.save(post);

        // Return response DTO
        return new CreatePostResponse(
                savedPost.getPostId(),
                savedPost.getUserId(),
                savedPost.getCommunityId(),
                savedPost.getPostTitle(),
                savedPost.getPostContent(),
                savedPost.getCreatedAt());
    }

    @Override
    public GetPostByIdResponse getPostById(GetPostByIdRequest request) {
        // Validate input parameters
        if (request.getPostId() == null) {
            throw new IllegalArgumentException("Post ID is required");
        }

        // Fetch post from database and validate existence
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Return response DTO
        return new GetPostByIdResponse(
                post.getPostId(),
                post.getUserId(),
                post.getCommunityId(),
                post.getPostTitle(),
                post.getPostContent(),
                post.getCreatedAt());
    }

    @Override
    public List<GetPostByIdResponse> getPostsByCommunityId(Long communityId) {
        if (communityId == null) {
            throw new IllegalArgumentException("Community ID is required");
        }
        
        List<Post> posts = postRepository.findByCommunityId(communityId);
        List<GetPostByIdResponse> responses = new ArrayList<>();
        
        for (Post post : posts) {
            responses.add(new GetPostByIdResponse(
                post.getPostId(),
                post.getUserId(),
                post.getCommunityId(),
                post.getPostTitle(),
                post.getPostContent(),
                post.getCreatedAt()
            ));
        }
        
        return responses;
    }

    @Override
    @Transactional
    public EditPostResponse editPost(HttpSession session, Long postId, EditPostRequest request) {
        // Get current user ID from session
        CurrentUserResponse currentUser = authService.getCurrentUser(session);
        if (!currentUser.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        Long userId = currentUser.userId();

        // Validate input parameters
        if (postId == null) {
            throw new IllegalArgumentException("Post ID is required");
        }
        if (request.getPostTitle() == null || request.getPostTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Post title is required");
        }
        if (request.getPostContent() == null || request.getPostContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Post content is required");
        }

        // Validate post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Validate post ownership
        if (!post.getUserId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the post");
        }

        // Store old values for response
        String oldPostTitle = post.getPostTitle();
        String oldPostContent = post.getPostContent();

        // Update post using raw SQL query
        int rowsUpdated = postRepository.updatePost(postId, request.getPostTitle(), request.getPostContent());

        if (rowsUpdated == 0) {
            throw new IllegalStateException("Failed to update post");
        }

        // Return response DTO
        return new EditPostResponse(
                postId,
                oldPostTitle,
                request.getPostTitle(),
                oldPostContent,
                request.getPostContent(),
                LocalDateTime.now());
    }

    @Override
    @Transactional
    public DeletePostResponse deletePost(HttpSession session, Long postId) {
        // Get current user ID from session
        CurrentUserResponse currentUser = authService.getCurrentUser(session);
        if (!currentUser.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        Long userId = currentUser.userId();

        // Validate input parameters
        if (postId == null) {
            throw new IllegalArgumentException("Post ID is required");
        }

        // Validate post exists
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Validate post ownership
        if (!post.getUserId().equals(userId)) {
            throw new ForbiddenException("User is not the owner of the post");
        }

        // Delete post using raw SQL query
        int rowsDeleted = postRepository.deleteByPostId(postId);

        if (rowsDeleted == 0) {
            throw new IllegalStateException("Failed to delete post");
        }

        // Return response DTO
        return new DeletePostResponse(
                postId,
                LocalDateTime.now(),
                "Post deleted successfully");
    }
}
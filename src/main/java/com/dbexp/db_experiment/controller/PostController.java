package com.dbexp.db_experiment.controller;

import java.net.URI;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.dbexp.db_experiment.dto.post.CreatePostRequest;
import com.dbexp.db_experiment.dto.post.CreatePostResponse;
import com.dbexp.db_experiment.dto.post.DeletePostResponse;
import com.dbexp.db_experiment.dto.post.EditPostRequest;
import com.dbexp.db_experiment.dto.post.EditPostResponse;
import com.dbexp.db_experiment.dto.post.GetPostByIdRequest;
import com.dbexp.db_experiment.dto.post.GetPostByIdResponse;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.service.PostService;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Post Management", description = "Endpoints for managing posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post by ID", description = "Retrieves post information by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Post found", content = @Content(schema = @Schema(implementation = GetPostByIdResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid post ID provided")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getPostById(
            @Parameter(description = "ID of the post to retrieve", example = "1") @PathVariable Long postId) {
        try {
            GetPostByIdResponse response = postService.getPostById(new GetPostByIdRequest(postId));
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching the post");
        }
    }

    @PostMapping
    @Operation(summary = "Create a new post", description = "Creates a new post in a community")
    @ApiResponse(responseCode = "201", description = "Post created successfully", content = @Content(schema = @Schema(implementation = CreatePostResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "Community not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> createPost(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "Post creation request payload") @Valid @RequestBody CreatePostRequest request) {
        try {
            CreatePostResponse response = postService.createPost(session, request);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.getPostId())
                    .toUri();
            return ResponseEntity.created(location).body(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while creating the post");
        }
    }

    @PutMapping("/{postId}")
    @Operation(summary = "Edit post", description = "Updates the content of an existing post")
    @ApiResponse(responseCode = "200", description = "Post updated successfully", content = @Content(schema = @Schema(implementation = EditPostResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "User not authorized to edit this post")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> editPost(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the post to edit", example = "1") @PathVariable Long postId,
            @Parameter(description = "Post edit request payload") @Valid @RequestBody EditPostRequest request) {
        if (postId == null || postId <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post ID must be provided and positive.");
        }
        try {
            EditPostResponse response = postService.editPost(session, postId, request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while editing the post");
        }
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post", description = "Deletes an existing post")
    @ApiResponse(responseCode = "200", description = "Post deleted successfully", content = @Content(schema = @Schema(implementation = DeletePostResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "User not authorized to delete this post")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deletePost(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the post to delete", example = "1") @PathVariable Long postId) {
        if (postId == null || postId <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post ID must be provided and positive.");
        }
        try {
            DeletePostResponse response = postService.deletePost(session, postId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while deleting the post");
        }
    }
}
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
import java.util.List;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.dbexp.db_experiment.dto.comment.CreateCommentRequest;
import com.dbexp.db_experiment.dto.comment.CreateCommentResponse;
import com.dbexp.db_experiment.dto.comment.DeleteCommentResponse;
import com.dbexp.db_experiment.dto.comment.EditCommentRequest;
import com.dbexp.db_experiment.dto.comment.EditCommentResponse;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdRequest;
import com.dbexp.db_experiment.dto.comment.GetCommentByIdResponse;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.service.CommentService;

@RestController
@RequestMapping("/api/comments")
@Tag(name = "Comment Management", description = "Endpoints for managing comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{commentId}")
    @Operation(summary = "Get comment by ID", description = "Retrieves comment information by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Comment found", content = @Content(schema = @Schema(implementation = GetCommentByIdResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid comment ID provided")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getCommentById(
            @Parameter(description = "ID of the comment to retrieve", example = "1") @PathVariable Long commentId) {
        try {
            GetCommentByIdResponse response = commentService.getById(new GetCommentByIdRequest(commentId));
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching the comment");
        }
    }

    @PostMapping
    @Operation(summary = "Create a new comment", description = "Creates a new comment under a post or as a reply to another comment")
    @ApiResponse(responseCode = "201", description = "Comment created successfully", content = @Content(schema = @Schema(implementation = CreateCommentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "Post or parent comment not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> createComment(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "Comment creation request payload") @Valid @RequestBody CreateCommentRequest request) {
        try {
            CreateCommentResponse response;
            if (request.getParentCommentId() == null) {
                response = commentService.createUnderPost(session, request);
            } else {
                response = commentService.createUnderComment(session, request);
            }
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.getCommentId())
                    .toUri();
            return ResponseEntity.created(location).body(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while creating the comment");
        }
    }

    @PutMapping("/{commentId}")
    @Operation(summary = "Edit comment", description = "Updates the content of an existing comment")
    @ApiResponse(responseCode = "200", description = "Comment updated successfully", content = @Content(schema = @Schema(implementation = EditCommentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "User not authorized to edit this comment")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> editComment(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the comment to edit", example = "1") @PathVariable Long commentId,
            @Parameter(description = "Comment edit request payload") @Valid @RequestBody EditCommentRequest request) {
        if (commentId == null || commentId <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment ID must be provided and positive.");
        }
        try {
            EditCommentResponse response = commentService.edit(session, commentId, request);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while editing the comment");
        }
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment", description = "Deletes an existing comment")
    @ApiResponse(responseCode = "200", description = "Comment deleted successfully", content = @Content(schema = @Schema(implementation = DeleteCommentResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "User not authorized to delete this comment")
    @ApiResponse(responseCode = "404", description = "Comment not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deleteComment(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the comment to delete", example = "1") @PathVariable Long commentId) {
        if (commentId == null || commentId <= 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment ID must be provided and positive.");
        }
        try {
            DeleteCommentResponse response = commentService.delete(session, commentId);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while deleting the comment");
        }
    }
    @GetMapping("/post/{postId}")
    @Operation(summary = "Get comments by post ID", description = "Retrieves all comments for a post")
    @ApiResponse(responseCode = "200", description = "Comments found", content = @Content(schema = @Schema(implementation = GetCommentByIdResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid post ID provided")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getCommentsByPostId(
            @Parameter(description = "ID of the post", example = "1") @PathVariable Long postId) {
        try {
            List<GetCommentByIdResponse> responses = commentService.getCommentsByPostId(postId);
            return ResponseEntity.ok(responses);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching comments for the post");
        }
    }
}
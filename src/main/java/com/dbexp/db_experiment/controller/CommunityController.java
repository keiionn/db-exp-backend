package com.dbexp.db_experiment.controller;

import java.net.URI;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.dbexp.db_experiment.dto.community.ChangeDescriptionRequest;
import com.dbexp.db_experiment.dto.community.ChangeDescriptionResponse;
import com.dbexp.db_experiment.dto.community.CreateCommunityRequest;
import com.dbexp.db_experiment.dto.community.CreateCommunityResponse;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByIdResponse;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameRequest;
import com.dbexp.db_experiment.dto.community.GetCommunityByNameResponse;
import com.dbexp.db_experiment.dto.post.GetPostByIdResponse;
import com.dbexp.db_experiment.exception.ConflictException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.service.CommunityService;
import com.dbexp.db_experiment.service.PostService;

@RestController
@RequestMapping("/api/communities")
@Tag(name = "Community Management", description = "Endpoints for managing communities")
public class CommunityController {

    private final CommunityService communityService;
    private final PostService postService;

    public CommunityController(CommunityService communityService, PostService postService) {
        this.communityService = communityService;
        this.postService = postService;
    }

    @GetMapping("/{communityId}")
    @Operation(summary = "Get community by ID", description = "Retrieves community information by its unique identifier")
    @ApiResponse(responseCode = "200", description = "Community found", content = @Content(schema = @Schema(implementation = GetCommunityByIdResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid community ID provided")
    @ApiResponse(responseCode = "404", description = "Community not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getCommunityById(
            @Parameter(description = "ID of the community to retrieve", example = "1") @PathVariable Long communityId) {
        try {
            GetCommunityByIdResponse response = communityService
                    .getCommunityById(new GetCommunityByIdRequest(communityId));
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching the community");
        }
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get community by name", description = "Retrieves community information by its name")
    @ApiResponse(responseCode = "200", description = "Community found", content = @Content(schema = @Schema(implementation = GetCommunityByNameResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid community name provided")
    @ApiResponse(responseCode = "404", description = "Community not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getCommunityByName(
            @Parameter(description = "Name of the community to retrieve", example = "asoiaf") @PathVariable String name) {
        try {
            GetCommunityByNameResponse response = communityService
                    .getCommunityByName(new GetCommunityByNameRequest(name));
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching the community by name");
        }
    }

    @PostMapping
    @Operation(summary = "Create a new community", description = "Creates a new community with the provided name and description")
    @ApiResponse(responseCode = "201", description = "Community created successfully", content = @Content(schema = @Schema(implementation = CreateCommunityResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "409", description = "Community name already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> createCommunity(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "Community creation request payload") @Valid @RequestBody CreateCommunityRequest request) {
        try {
            CreateCommunityResponse response = communityService.createCommunity(session, request);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.getCommunityId())
                    .toUri();
            return ResponseEntity.created(location).body(response);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while creating the community");
        }
    }

    @PutMapping("/{communityId}/description")
    @Operation(summary = "Change community description", description = "Updates the description of an existing community")
    @ApiResponse(responseCode = "200", description = "Description updated successfully", content = @Content(schema = @Schema(implementation = ChangeDescriptionResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "404", description = "Community not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> changeDescription(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the community whose description to change", example = "1") @PathVariable Long communityId,
            @Parameter(description = "Description change request payload") @Valid @RequestBody ChangeDescriptionRequest request) {
        try {
            ChangeDescriptionResponse response = communityService.changeDescription(session, communityId, request);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while changing the description");
        }
    }

    @GetMapping("/latest")
    @Operation(summary = "Get latest communities", description = "Retrieves the latest 20 communities")
    @ApiResponse(responseCode = "200", description = "Communities found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetCommunityByIdResponse.class))))
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getLatestCommunities() {
        try {
            List<GetCommunityByIdResponse> responses = communityService.getLatestCommunities();
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching the latest communities");
        }
    }

    @GetMapping("/{communityId}/posts")
    @Operation(summary = "Get posts by community ID", description = "Retrieves all posts in a community")
    @ApiResponse(responseCode = "200", description = "Posts found", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GetPostByIdResponse.class))))
    @ApiResponse(responseCode = "400", description = "Invalid community ID provided")
    @ApiResponse(responseCode = "404", description = "Community not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getPostsByCommunityId(
            @Parameter(description = "ID of the community", example = "1") @PathVariable Long communityId) {
        try {
            List<GetPostByIdResponse> responses = postService.getPostsByCommunityId(communityId);
            return ResponseEntity.ok(responses);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching posts for the community");
        }
    }
}
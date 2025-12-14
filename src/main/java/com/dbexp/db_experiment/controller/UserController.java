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

import com.dbexp.db_experiment.dto.user.ChangeEmailRequest;
import com.dbexp.db_experiment.dto.user.ChangeEmailResponse;
import com.dbexp.db_experiment.dto.user.ChangePasswordRequest;
import com.dbexp.db_experiment.dto.user.ChangePasswordResponse;
import com.dbexp.db_experiment.dto.user.ChangeUsernameRequest;
import com.dbexp.db_experiment.dto.user.ChangeUsernameResponse;
import com.dbexp.db_experiment.dto.user.CreateUserRequest;
import com.dbexp.db_experiment.dto.user.CreateUserResponse;
import com.dbexp.db_experiment.dto.user.DeleteAccountRequest;
import com.dbexp.db_experiment.dto.user.DeleteAccountResponse;
import com.dbexp.db_experiment.dto.user.GetUserByIdRequest;
import com.dbexp.db_experiment.dto.user.GetUserByIdResponse;
import com.dbexp.db_experiment.dto.user.GetUserByUsernameRequest;
import com.dbexp.db_experiment.dto.user.GetUserByUsernameResponse;
import com.dbexp.db_experiment.exception.ConflictException;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.service.UserService;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Retrieves user information by their unique identifier")
    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = GetUserByIdResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid user ID provided")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "1") @PathVariable Long userId) {
        try {
            GetUserByIdResponse response = userService.getUserById(new GetUserByIdRequest(userId));
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching the user");
        }
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves user information by their username")
    @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = GetUserByUsernameResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid username provided")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> getUserByUsername(
            @Parameter(description = "Username of the user to retrieve", example = "alice") @PathVariable String username) {
        try {
            GetUserByUsernameResponse response = userService.getUserByUsername(new GetUserByUsernameRequest(username));
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while fetching the user by username");
        }
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user account with the provided details")
    @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = CreateUserResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "409", description = "Username or email already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> createUser(
            @Parameter(description = "User creation request payload") @Valid @RequestBody CreateUserRequest request) {
        try {
            CreateUserResponse response = userService.createUser(request);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(response.getUserId())
                    .toUri();
            return ResponseEntity.created(location).body(response);
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while creating the user");
        }
    }

    @PutMapping("/{userId}/username")
    @Operation(summary = "Change username", description = "Updates the username for a specific user")
    @ApiResponse(responseCode = "200", description = "Username updated successfully", content = @Content(schema = @Schema(implementation = ChangeUsernameResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "Cannot modify other user's account")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "Username already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> changeUsername(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the user whose username to change", example = "1") @PathVariable Long userId,
            @Parameter(description = "New username request payload") @Valid @RequestBody ChangeUsernameRequest request) {
        try {
            ChangeUsernameResponse response = userService.changeUsername(session, userId, request);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while changing the username");
        }
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "Change password", description = "Updates the password for a specific user")
    @ApiResponse(responseCode = "200", description = "Password updated successfully", content = @Content(schema = @Schema(implementation = ChangePasswordResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "Cannot modify other user's account")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> changePassword(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the user whose password to change", example = "1") @PathVariable Long userId,
            @Parameter(description = "New password request payload") @Valid @RequestBody ChangePasswordRequest request) {
        try {
            ChangePasswordResponse response = userService.changePassword(session, userId, request);
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
                    .body("An error occurred while changing password");
        }
    }

    @PutMapping("/{userId}/email")
    @Operation(summary = "Change email", description = "Updates the email for a specific user")
    @ApiResponse(responseCode = "200", description = "Email updated successfully", content = @Content(schema = @Schema(implementation = ChangeEmailResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "401", description = "User not authenticated")
    @ApiResponse(responseCode = "403", description = "Cannot modify other user's account")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "409", description = "Email already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> changeEmail(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the user whose email to change", example = "1") @PathVariable Long userId,
            @Parameter(description = "New email request payload") @Valid @RequestBody ChangeEmailRequest request) {
        try {
            ChangeEmailResponse response = userService.changeEmail(session, userId, request);
            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ConflictException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("An error occurred while changing email");
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user account", description = "Deletes a user account permanently")
    @ApiResponse(responseCode = "200", description = "User deleted successfully", content = @Content(schema = @Schema(implementation = DeleteAccountResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data provided")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<?> deleteAccount(
            @Parameter(description = "HTTP session for authentication") HttpSession session,
            @Parameter(description = "ID of the user to delete", example = "1") @PathVariable Long userId,
            @Parameter(description = "Account deletion confirmation payload") @Valid @RequestBody DeleteAccountRequest request) {
        try {
            DeleteAccountResponse response = userService.deleteAccount(session, userId, request);
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
                    .body("An error occurred while deleting the account");
        }
    }
}
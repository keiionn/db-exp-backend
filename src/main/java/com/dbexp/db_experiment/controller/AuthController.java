package com.dbexp.db_experiment.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.auth.LoginRequest;
import com.dbexp.db_experiment.dto.auth.LoginResponse;
import com.dbexp.db_experiment.dto.auth.LogoutResponse;
import com.dbexp.db_experiment.service.AuthService;

@Tag(name = "Authentication", description = "Operations related to user authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns login response")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful login", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody @Schema(description = "Login credentials") LoginRequest request,
            HttpSession session) {
        try {
            LoginResponse response = authService.login(request, session);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @Operation(summary = "User logout", description = "Invalidates the user's session")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(implementation = LogoutResponse.class)))
    })
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpSession session) {
        LogoutResponse response = authService.logout(session);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get current user", description = "Retrieves the currently authenticated user's details")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user details", content = @Content(schema = @Schema(implementation = CurrentUserResponse.class)))
    })
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(HttpSession session) {
        CurrentUserResponse response = authService.getCurrentUser(session);
        return ResponseEntity.ok(response);
    }
}
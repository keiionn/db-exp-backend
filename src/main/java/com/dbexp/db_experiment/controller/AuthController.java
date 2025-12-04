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

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.auth.LoginRequest;
import com.dbexp.db_experiment.dto.auth.LoginResponse;
import com.dbexp.db_experiment.dto.auth.LogoutResponse;
import com.dbexp.db_experiment.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        try {
            LoginResponse response = authService.login(request, session);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(HttpSession session) {
        LogoutResponse response = authService.logout(session);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(HttpSession session) {
        CurrentUserResponse response = authService.getCurrentUser(session);
        return ResponseEntity.ok(response);
    }
}
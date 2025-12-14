package com.dbexp.db_experiment.dto.user;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class GetUserByIdResponse {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long userId;

    @Schema(description = "Username of the user", example = "alice")
    private String username;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Timestamp when the user account was created", example = "2025-11-25T14:30:00")
    private LocalDateTime creationTime;

    // Constructors
    public GetUserByIdResponse(Long userId, String username, String email, LocalDateTime creationTime) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.creationTime = creationTime;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}
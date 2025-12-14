package com.dbexp.db_experiment.dto.user;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangeUsernameResponse {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long userId;

    @Schema(description = "Previous username of the user", example = "alice")
    private String oldUsername;

    @Schema(description = "New username of the user", example = "jane_doe")
    private String newUsername;

    @Schema(description = "Timestamp when the username was changed", example = "2025-11-25T14:30:00")
    private LocalDateTime updatedAt;

    // Constructors
    public ChangeUsernameResponse(Long userId, String oldUsername, String newUsername, LocalDateTime updatedAt) {
        this.userId = userId;
        this.oldUsername = oldUsername;
        this.newUsername = newUsername;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
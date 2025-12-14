package com.dbexp.db_experiment.dto.user;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangePasswordResponse {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long userId;

    @Schema(description = "Timestamp when the password was changed", example = "2025-11-25T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Success message", example = "Password changed successfully")
    private String message;

    // Constructors
    public ChangePasswordResponse(Long userId, LocalDateTime updatedAt, String message) {
        this.userId = userId;
        this.updatedAt = updatedAt;
        this.message = message;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
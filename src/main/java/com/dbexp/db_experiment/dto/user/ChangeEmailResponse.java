package com.dbexp.db_experiment.dto.user;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangeEmailResponse {

    @Schema(description = "Unique identifier of the user", example = "1")
    private Long userId;

    @Schema(description = "Previous email address of the user", example = "john.doe@example.com")
    private String oldEmail;

    @Schema(description = "New email address of the user", example = "jane.doe@example.com")
    private String newEmail;

    @Schema(description = "Timestamp when the email was changed", example = "2025-11-25T14:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Success message", example = "Email changed successfully")
    private String message;

    // Constructors
    public ChangeEmailResponse(Long userId, String oldEmail, String newEmail, LocalDateTime updatedAt, String message) {
        this.userId = userId;
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
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

    public String getOldEmail() {
        return oldEmail;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
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
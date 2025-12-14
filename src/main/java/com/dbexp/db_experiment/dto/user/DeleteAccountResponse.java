package com.dbexp.db_experiment.dto.user;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class DeleteAccountResponse {

    @Schema(description = "Unique identifier of the deleted user", example = "1")
    private Long userId;

    @Schema(description = "Timestamp when the account was deleted", example = "2025-11-25T14:30:00")
    private LocalDateTime deletedAt;

    @Schema(description = "Success message", example = "Account deleted successfully")
    private String message;

    // Constructors
    public DeleteAccountResponse(Long userId, LocalDateTime deletedAt, String message) {
        this.userId = userId;
        this.deletedAt = deletedAt;
        this.message = message;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
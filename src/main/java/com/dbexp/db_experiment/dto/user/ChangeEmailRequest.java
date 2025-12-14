package com.dbexp.db_experiment.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangeEmailRequest {

    @NotBlank(message = "Current password is required")
    @Schema(description = "Current password of the user", example = "password123")
    private String currentPassword;

    @NotBlank(message = "New email is required")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "New email address for the user", example = "jane.doe@example.com")
    private String newEmail;

    // Constructors
    public ChangeEmailRequest(String currentPassword, String newEmail) {
        this.currentPassword = currentPassword;
        this.newEmail = newEmail;
    }

    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
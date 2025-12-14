package com.dbexp.db_experiment.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public class ChangeUsernameRequest {

    @NotBlank(message = "New username is required")
    @Size(min = 3, max = 255, message = "Username must be between 3 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username must contain only alphanumeric characters and underscores")
    @Schema(description = "New username for the user account", example = "jane_doe", minLength = 3, maxLength = 255)
    private String newUsername;

    // Constructors
    public ChangeUsernameRequest(String newUsername) {
        this.newUsername = newUsername;
    }

    // Getters and Setters
    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
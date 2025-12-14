package com.dbexp.db_experiment.dto.user;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public class DeleteAccountRequest {

    @NotBlank(message = "Password is required for account deletion")
    @Schema(description = "User's password for account deletion confirmation", example = "password123")
    private String password;

    // Constructors
    public DeleteAccountRequest(String password) {
        this.password = password;
    }

    // Getters and Setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
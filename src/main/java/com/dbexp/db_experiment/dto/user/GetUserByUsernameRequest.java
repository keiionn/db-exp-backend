package com.dbexp.db_experiment.dto.user;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public class GetUserByUsernameRequest {
    @NotBlank(message = "Username cannot be blank")
    @Schema(description = "Username of the user to retrieve", example = "alice")
    private String username;

    // Constructors
    public GetUserByUsernameRequest(String username) {
        this.username = username;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
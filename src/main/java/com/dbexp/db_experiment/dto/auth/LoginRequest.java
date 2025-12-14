package com.dbexp.db_experiment.dto.auth;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @NotBlank(message = "Username is required") @Schema(description = "Username of the user to login", example = "alice") String username,

        @NotBlank(message = "Password is required") @Schema(description = "Password of the user to login", example = "password123") String password) {
}
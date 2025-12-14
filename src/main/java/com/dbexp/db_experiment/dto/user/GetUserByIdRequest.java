package com.dbexp.db_experiment.dto.user;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

public class GetUserByIdRequest {
    @NotBlank(message = "User ID cannot be blank")
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long userId;

    // Constructors
    public GetUserByIdRequest(Long userId) {
        this.userId = userId;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

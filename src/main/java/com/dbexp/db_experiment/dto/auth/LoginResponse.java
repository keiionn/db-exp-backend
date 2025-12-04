package com.dbexp.db_experiment.dto.auth;

public record LoginResponse(
        Long userId,
        String username,
        String email,
        String message) {
}
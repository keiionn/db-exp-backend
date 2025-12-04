package com.dbexp.db_experiment.dto.auth;

public record CurrentUserResponse(
        boolean authenticated,
        Long userId,
        String username,
        String email) {
    public static CurrentUserResponse notAuthenticated() {
        return new CurrentUserResponse(false, null, null, null);
    }
}
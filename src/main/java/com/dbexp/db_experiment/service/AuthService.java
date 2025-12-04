package com.dbexp.db_experiment.service;

import jakarta.servlet.http.HttpSession;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.auth.LoginRequest;
import com.dbexp.db_experiment.dto.auth.LoginResponse;
import com.dbexp.db_experiment.dto.auth.LogoutResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request, HttpSession session);

    LogoutResponse logout(HttpSession session);

    CurrentUserResponse getCurrentUser(HttpSession session);
}
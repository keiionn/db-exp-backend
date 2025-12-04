package com.dbexp.db_experiment.service;

import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.auth.LoginRequest;
import com.dbexp.db_experiment.dto.auth.LoginResponse;
import com.dbexp.db_experiment.dto.auth.LogoutResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.repository.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String USER_ID_SESSION_KEY = "userId";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginResponse login(LoginRequest request, HttpSession session) {
        Optional<User> userOptional = userRepository.findByUsername(request.username());

        if (userOptional.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        session.setAttribute(USER_ID_SESSION_KEY, user.getUserId());

        return new LoginResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                "Login successful");
    }

    @Override
    public LogoutResponse logout(HttpSession session) {
        session.invalidate();
        return new LogoutResponse("Logout successful");
    }

    @Override
    public CurrentUserResponse getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute(USER_ID_SESSION_KEY);

        if (userId == null) {
            return CurrentUserResponse.notAuthenticated();
        }

        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return CurrentUserResponse.notAuthenticated();
        }

        User user = userOptional.get();

        return new CurrentUserResponse(
                true,
                user.getUserId(),
                user.getUsername(),
                user.getEmail());
    }
}
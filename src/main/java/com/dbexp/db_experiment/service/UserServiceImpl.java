package com.dbexp.db_experiment.service;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dbexp.db_experiment.dto.auth.CurrentUserResponse;
import com.dbexp.db_experiment.dto.user.ChangeEmailRequest;
import com.dbexp.db_experiment.dto.user.ChangeEmailResponse;
import com.dbexp.db_experiment.dto.user.ChangePasswordRequest;
import com.dbexp.db_experiment.dto.user.ChangePasswordResponse;
import com.dbexp.db_experiment.dto.user.ChangeUsernameRequest;
import com.dbexp.db_experiment.dto.user.ChangeUsernameResponse;
import com.dbexp.db_experiment.dto.user.CreateUserRequest;
import com.dbexp.db_experiment.dto.user.CreateUserResponse;
import com.dbexp.db_experiment.dto.user.DeleteAccountRequest;
import com.dbexp.db_experiment.dto.user.DeleteAccountResponse;
import com.dbexp.db_experiment.dto.user.GetUserByIdRequest;
import com.dbexp.db_experiment.dto.user.GetUserByIdResponse;
import com.dbexp.db_experiment.dto.user.GetUserByUsernameRequest;
import com.dbexp.db_experiment.dto.user.GetUserByUsernameResponse;
import com.dbexp.db_experiment.entity.User;
import com.dbexp.db_experiment.exception.ConflictException;
import com.dbexp.db_experiment.exception.ForbiddenException;
import com.dbexp.db_experiment.exception.ResourceNotFoundException;
import com.dbexp.db_experiment.exception.UnauthorizedException;
import com.dbexp.db_experiment.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @Override
    public GetUserByIdResponse getUserById(GetUserByIdRequest request) {
        // Validate input parameters
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        // Fetch user from database and validate existence
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Return response DTO
        return new GetUserByIdResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt());
    }

    @Override
    public GetUserByUsernameResponse getUserByUsername(GetUserByUsernameRequest request) {
        // Validate input parameters
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        // Fetch user from database and validate existence
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Return response DTO
        return new GetUserByUsernameResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt());
    }

    @Override
    public CreateUserResponse createUser(CreateUserRequest request) {
        // Validate input parameters
        if (request.getUsername() == null) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getEmail() == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getPassword() == null) {
            throw new IllegalArgumentException("Password is required");
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        // Hash the password using Argon2
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create new User entity
        User user = new User(
                request.getUsername(),
                hashedPassword,
                request.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        // Save user to database
        User savedUser = userRepository.save(user);

        // Return response DTO
        return new CreateUserResponse(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt());
    }

    @Override
    @Transactional
    public ChangeUsernameResponse changeUsername(HttpSession session, Long userId, ChangeUsernameRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUserResponse = authService.getCurrentUser(session);
        if (!currentUserResponse.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        User user = userRepository.findById(currentUserResponse.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure user is modifying their own account
        if (!user.getUserId().equals(userId)) {
            throw new ForbiddenException("Cannot modify other user's account");
        }

        // Validate new username is different from current
        if (user.getUsername().equals(request.getNewUsername())) {
            throw new IllegalArgumentException("New username must be different from current username");
        }

        // Validate new username uniqueness
        if (userRepository.existsByUsername(request.getNewUsername())) {
            throw new ConflictException("Username already exists");
        }

        // Store old username for response
        String oldUsername = user.getUsername();

        // Update username using raw SQL query
        int rowsUpdated = userRepository.updateUsername(userId, request.getNewUsername());

        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("Failed to update username");
        }

        // Return response DTO
        return new ChangeUsernameResponse(
                userId,
                oldUsername,
                request.getNewUsername(),
                LocalDateTime.now());
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(HttpSession session, Long userId, ChangePasswordRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUserResponse = authService.getCurrentUser(session);
        if (!currentUserResponse.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        User user = userRepository.findById(currentUserResponse.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure user is modifying their own account
        if (!user.getUserId().equals(userId)) {
            throw new ForbiddenException("Cannot modify other user's account");
        }

        // Verify current password matches
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password is different from current
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Hash new password
        String newPasswordHash = passwordEncoder.encode(request.getNewPassword());

        // Update password using raw SQL query
        int rowsUpdated = userRepository.updatePassword(userId, newPasswordHash);

        if (rowsUpdated == 0) {
            throw new IllegalStateException("Failed to update password");
        }

        // Return response DTO
        return new ChangePasswordResponse(
                userId,
                LocalDateTime.now(),
                "Password changed successfully");
    }

    @Override
    @Transactional
    public ChangeEmailResponse changeEmail(HttpSession session, Long userId, ChangeEmailRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUserResponse = authService.getCurrentUser(session);
        if (!currentUserResponse.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        User user = userRepository.findById(currentUserResponse.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure user is modifying their own account
        if (!user.getUserId().equals(userId)) {
            throw new ForbiddenException("Cannot modify other user's account");
        }

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new email is different from current
        if (user.getEmail().equals(request.getNewEmail())) {
            throw new IllegalArgumentException("New email must be different from current email");
        }

        // Validate new email uniqueness
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new ConflictException("Email already exists");
        }

        // Store old email for response
        String oldEmail = user.getEmail();

        // Update email using raw SQL query
        int rowsUpdated = userRepository.updateEmail(userId, request.getNewEmail());

        if (rowsUpdated == 0) {
            throw new IllegalArgumentException("Failed to update email");
        }

        // Return response DTO
        return new ChangeEmailResponse(
                userId,
                oldEmail,
                request.getNewEmail(),
                LocalDateTime.now(),
                "Email changed successfully");
    }

    @Override
    @Transactional
    public DeleteAccountResponse deleteAccount(HttpSession session, Long userId, DeleteAccountRequest request) {
        // Validate session and get current user
        CurrentUserResponse currentUserResponse = authService.getCurrentUser(session);
        if (!currentUserResponse.authenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        User user = userRepository.findById(currentUserResponse.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure user is modifying their own account
        if (!user.getUserId().equals(userId)) {
            throw new ForbiddenException("Cannot modify other user's account");
        }

        // Verify current password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Delete user using raw SQL query
        int rowsDeleted = userRepository.deleteByUserId(userId);

        if (rowsDeleted == 0) {
            throw new IllegalStateException("Failed to delete account");
        }

        // Return response DTO
        return new DeleteAccountResponse(
                userId,
                LocalDateTime.now(),
                "Account deleted successfully");
    }
}
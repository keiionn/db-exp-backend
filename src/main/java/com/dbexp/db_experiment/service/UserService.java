package com.dbexp.db_experiment.service;

import jakarta.servlet.http.HttpSession;

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

public interface UserService {
    GetUserByIdResponse getUserById(GetUserByIdRequest request);

    GetUserByUsernameResponse getUserByUsername(GetUserByUsernameRequest request);

    CreateUserResponse createUser(CreateUserRequest request);

    ChangeUsernameResponse changeUsername(HttpSession session, Long userId, ChangeUsernameRequest request);

    ChangePasswordResponse changePassword(HttpSession session, Long userId, ChangePasswordRequest request);

    ChangeEmailResponse changeEmail(HttpSession session, Long userId, ChangeEmailRequest request);

    DeleteAccountResponse deleteAccount(HttpSession session, Long userId, DeleteAccountRequest request);
}
package com.tandem.auth_service.service.auth;

import java.util.UUID;

import com.tandem.auth_service.api.dto.UserDto;
import com.tandem.auth_service.api.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(String email, String rawPassword);
    UserDto getCurrentUser(UUID userId);
}
package com.tandem.auth_service.service.auth;

import java.util.UUID;

import com.tandem.auth_service.api.dto.UserDto;
import com.tandem.auth_service.api.dto.auth.response.LoginResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    LoginResponse login(HttpServletRequest httpRequest,String email, String rawPassword);
    UserDto getCurrentUser(UUID userId);
}
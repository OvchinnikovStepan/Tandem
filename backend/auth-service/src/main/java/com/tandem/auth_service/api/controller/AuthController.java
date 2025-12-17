package com.tandem.auth_service.api.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tandem.auth_service.api.dto.PasswordStrength;
import com.tandem.auth_service.api.dto.SessionDto;
import com.tandem.auth_service.api.dto.UserDto;
import com.tandem.auth_service.api.dto.request.CheckPasswordStrengthRequest;
import com.tandem.auth_service.api.dto.request.LoginRequest;
import com.tandem.auth_service.api.dto.request.RefreshTokenRequest;
import com.tandem.auth_service.api.dto.request.RegisterEmailRequest;
import com.tandem.auth_service.api.dto.request.RegisterPhoneRequest;
import com.tandem.auth_service.api.dto.request.VerifyPhoneRequest;
import com.tandem.auth_service.api.dto.response.CheckPasswordStrengthResponse;
import com.tandem.auth_service.api.dto.response.LoginResponse;
import com.tandem.auth_service.api.dto.response.LogoutResponse;
import com.tandem.auth_service.api.dto.response.RefreshTokenResponse;
import com.tandem.auth_service.api.dto.response.RegisterEmailResponse;
import com.tandem.auth_service.api.dto.response.RegisterPhoneResponse;
import com.tandem.auth_service.api.dto.response.VerifyPhoneResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register/phone")
    public RegisterPhoneResponse registerPhone(
        @Valid @RequestBody RegisterPhoneRequest request
    ) {
        return new RegisterPhoneResponse("stub-verification-id");
    }

    @PostMapping("/register/verify")
    public VerifyPhoneResponse verifyPhone(
            @Valid @RequestBody VerifyPhoneRequest request
    ) {
        return new VerifyPhoneResponse(true);
    }

    @PostMapping("/register/email")
    public RegisterEmailResponse registerEmail(
            @Valid @RequestBody RegisterEmailRequest request
    ) {
        return new RegisterEmailResponse(
                "user-id",
                "access-token",
                "refresh-token",
                PasswordStrength.GOOD
        );
    }

    @PostMapping("/password/check-strength")
    public CheckPasswordStrengthResponse checkPassword(
            @Valid @RequestBody CheckPasswordStrengthRequest request
    ) {
        return new CheckPasswordStrengthResponse(
                PasswordStrength.GOOD,
                4,
                List.of(),
                Map.of()
        );
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return new LoginResponse("access-token", "refresh-token");
    }

    @PostMapping("/logout")
    public LogoutResponse logout() {
        return new LogoutResponse(true);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return new RefreshTokenResponse("new-access-token");
    }

    @GetMapping("/me")
    public Map<String, UserDto> me() {
        return Map.of(
                "user",
                new UserDto(
                        UUID.randomUUID(),
                        "test@example.com",
                        "+79990000000",
                        true,
                        true,
                        "ACTIVE",
                        LocalDateTime.now(),
                        null
                )
        );
    }

    @GetMapping("/sessions")
    public Map<String, List<SessionDto>> sessions() {
        return Map.of("sessions", List.of());
    }

    @DeleteMapping("/sessions/{sessionId}")
    public LogoutResponse terminateSession(
            @Valid @PathVariable UUID sessionId
    ) {
        return new LogoutResponse(true);
    }
}

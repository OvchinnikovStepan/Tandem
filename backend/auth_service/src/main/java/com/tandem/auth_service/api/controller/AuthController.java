package com.tandem.auth_service.api.controller;

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
import org.springframework.security.core.Authentication;


import com.tandem.auth_service.api.dto.PasswordStrength;
import com.tandem.auth_service.api.dto.RefreshResultDto;
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
import com.tandem.auth_service.api.dto.response.MeResponse;
import com.tandem.auth_service.api.dto.response.RefreshTokenResponse;
import com.tandem.auth_service.api.dto.response.RegisterEmailResponse;
import com.tandem.auth_service.api.dto.response.RegisterPhoneResponse;
import com.tandem.auth_service.api.dto.response.VerifyPhoneResponse;
import com.tandem.auth_service.security.AuthPrincipal;
import com.tandem.auth_service.service.auth.AuthService;
import com.tandem.auth_service.service.registration.RegistrationService;
import com.tandem.auth_service.service.session.SessionService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RegistrationService registrationService;
    private final SessionService sessionService;

    public AuthController(RegistrationService registrationService, AuthService authService,SessionService sessionService) {
        this.authService = authService;
        this.registrationService = registrationService;
        this.sessionService=sessionService;
    }

    @PostMapping("/register/phone")
    public RegisterPhoneResponse registerPhone(
                @Valid @RequestBody RegisterPhoneRequest request
        ) {
            UUID verificationId =
                registrationService.startPhoneRegistration(request.phoneNumber());

            return new RegisterPhoneResponse(verificationId.toString());
    }

    @PostMapping("/register/verify")
    public VerifyPhoneResponse verifyPhone(
            @Valid @RequestBody VerifyPhoneRequest request
    ) {
        registrationService.verifyPhone(
            request.verificationId(),
            request.code()
        );

        return new VerifyPhoneResponse(true);
    }


    @PostMapping("/register/email")
    public RegisterEmailResponse registerEmail(
            @Valid @RequestBody RegisterEmailRequest request,
            HttpServletRequest httpRequest
    ) {
        return registrationService.completeRegistration(
                httpRequest,
                request.verificationId(),
                request.email(),
                request.password()
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
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.login(
                httpRequest,
                request.email(),
                request.password()
        );
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public LogoutResponse logout(
        Authentication authentication
    ) {
        AuthPrincipal principal =
            (AuthPrincipal) authentication.getPrincipal();

        sessionService.logout(principal.sessionId());
        return new LogoutResponse(true);
    }

    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        RefreshResultDto refreshResult = sessionService.refresh(request.refreshToken());
        return new RefreshTokenResponse(refreshResult.accessToken(),refreshResult.refreshToken());
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {

        AuthPrincipal principal =
                (AuthPrincipal) authentication.getPrincipal();

        UserDto user = authService.getCurrentUser(principal.userId());

        return new MeResponse(user);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/sessions")
    public Map<String, List<SessionDto>> sessions(Authentication auth) {
    AuthPrincipal principal = (AuthPrincipal) auth.getPrincipal();
    return Map.of(
        "sessions",
        sessionService.getActiveSessions(principal.userId())
    );
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/sessions/{sessionId}")
    public LogoutResponse terminateSession(
            @Valid @PathVariable UUID sessionId,
            Authentication auth
    ) {
        AuthPrincipal principal = (AuthPrincipal) auth.getPrincipal();
        sessionService.terminateSession(principal.userId(), sessionId);
        return new LogoutResponse(true);
    }
}

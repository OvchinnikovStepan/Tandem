package com.tandem.auth_service.api.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;

import com.tandem.auth_service.api.dto.password_reset.request.CompleteResetPasswordRequest;
import com.tandem.auth_service.api.dto.password_reset.request.ResetPasswordRequest;
import com.tandem.auth_service.api.dto.password_reset.request.VerifyResetTokenRequest;


import com.tandem.auth_service.api.dto.password_reset.response.*;
import com.tandem.auth_service.service.password_reset.PasswordResetService;

@RestController
@RequestMapping("/api/auth/password/reset")
public class PasswordResetController {

    private final PasswordResetService service;

    public PasswordResetController(PasswordResetService service) {
        this.service = service;
    }

    @PostMapping("/request")
    public ResetPasswordRequestResponse request(
            @RequestBody ResetPasswordRequest request,
            HttpServletRequest http) {

        return service.requestReset(
                request,
                http.getRemoteAddr());
    }

    @PostMapping("/verify")
    public VerifyResetTokenResponse verify(
            @RequestBody VerifyResetTokenRequest request) {

        return service.verifyToken(request.resetToken());
    }

    @PostMapping("/complete")
    public CompleteResetPasswordResponse complete(
            @RequestBody CompleteResetPasswordRequest request,
            HttpServletRequest http) {

        return service.completeReset(
                request,
                http.getRemoteAddr());
    }
}
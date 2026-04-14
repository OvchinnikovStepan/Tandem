package com.tandem.auth_service.api.dto.password_reset.response;

import java.util.List;

import com.tandem.auth_service.api.dto.password_reset.common.LocalizedMessage;
import com.tandem.auth_service.api.dto.password_strength.common.PasswordStrength;

public record CompleteResetPasswordResponse(

        boolean success,
        int sessionsTerminated,
        String error,
        PasswordStrength passwordStrength,
        LocalizedMessage message,
        List<LocalizedMessage> feedback

) {

    public static CompleteResetPasswordResponse validToken(boolean b, int i,PasswordStrength passwordStrength) {
        return new CompleteResetPasswordResponse(b, i, null, passwordStrength, null, null);
    }

    public static CompleteResetPasswordResponse invalidToken() {
        return new CompleteResetPasswordResponse(false, 0, null, null, null, null);
    }}
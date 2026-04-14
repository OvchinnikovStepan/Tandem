package com.tandem.auth_service.api.dto.password_reset.response;

import com.tandem.auth_service.api.dto.password_reset.common.LocalizedMessage;

public record VerifyResetTokenResponse(

        boolean valid,
        String resetId,
        String email,
        long expiresIn,
        String error,
        LocalizedMessage message

) {

    public static VerifyResetTokenResponse invalid() {
        return new VerifyResetTokenResponse(false,"","",0,"Invalid code",null);
    }

    public static VerifyResetTokenResponse valid(String resetId2, String maskEmail, int i) {
        return new VerifyResetTokenResponse(true,resetId2,maskEmail,i,"Valid code",null);
    }}
package com.tandem.auth_service.api.dto.password_reset.response;

import com.tandem.auth_service.api.dto.password_reset.common.LocalizedMessage;

public record ResetPasswordRequestResponse(

        String resetId,
        String method,
        String sentTo,
        long expiresIn,
        LocalizedMessage message

) {}
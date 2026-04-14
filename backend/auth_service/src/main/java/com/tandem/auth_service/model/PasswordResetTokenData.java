package com.tandem.auth_service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResetTokenData {

    private String resetId;
    private String userId;
    private String email;
    private String method;
    private String locale;
}
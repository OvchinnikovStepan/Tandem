package com.tandem.auth_service.service.password_reset;

import com.tandem.auth_service.api.dto.password_reset.request.CompleteResetPasswordRequest;
import com.tandem.auth_service.api.dto.password_reset.request.ResetPasswordRequest;
import com.tandem.auth_service.api.dto.password_reset.response.CompleteResetPasswordResponse;
import com.tandem.auth_service.api.dto.password_reset.response.ResetPasswordRequestResponse;
import com.tandem.auth_service.api.dto.password_reset.response.VerifyResetTokenResponse;

public interface PasswordResetService {

    ResetPasswordRequestResponse requestReset(ResetPasswordRequest request, String ip);

    VerifyResetTokenResponse verifyToken(String token);

    CompleteResetPasswordResponse completeReset(CompleteResetPasswordRequest request, String ip);
}
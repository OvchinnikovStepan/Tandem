package com.tandem.auth_service.service.password_strength;

import com.tandem.auth_service.api.dto.password_strength.common.PasswordStrengthResult;

public interface PasswordStrengthService {

    PasswordStrengthResult evaluate(String password);
}

package com.tandem.auth_service.service.password;

import com.tandem.auth_service.api.dto.password.PasswordStrengthResult;

public interface PasswordStrengthService {

    PasswordStrengthResult evaluate(String password);
}

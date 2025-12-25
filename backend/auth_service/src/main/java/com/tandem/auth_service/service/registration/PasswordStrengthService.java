package com.tandem.auth_service.service.registration;

import com.tandem.auth_service.api.dto.PasswordStrength;
import org.springframework.stereotype.Service;

@Service
public class PasswordStrengthService {

    public PasswordStrength evaluate(String password) {
        if (password.length() < 6) {
            return PasswordStrength.BAD;
        }
        if (password.length() < 8) {
            return PasswordStrength.WEAK;
        }
        if (password.length() < 10) {
            return PasswordStrength.STANDARD;
        }
        if (password.length() < 12) {
            return PasswordStrength.GOOD;
        }
        return PasswordStrength.STRONG;
    }
}

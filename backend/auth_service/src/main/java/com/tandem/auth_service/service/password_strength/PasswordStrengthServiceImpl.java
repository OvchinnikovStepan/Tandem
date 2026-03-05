package com.tandem.auth_service.service.password_strength;

import org.springframework.stereotype.Service;

import com.tandem.auth_service.api.dto.password_strength.common.PasswordStrength;
import com.tandem.auth_service.api.dto.password_strength.common.PasswordStrengthResult;
import com.tandem.auth_service.utils.PasswordStrengthUtil;

@Service
public class PasswordStrengthServiceImpl implements PasswordStrengthService {

    public PasswordStrengthResult evaluate(String password) {

        var eval = PasswordStrengthUtil.evaluate(password);

        var strength = PasswordStrength.fromScore(eval.score());

        boolean valid = strength != PasswordStrength.BAD;

        return new PasswordStrengthResult(
                eval.score(),
                strength,
                valid,
                eval.feedback(),
                eval.warnings()
        );
    }
}
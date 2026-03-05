package com.tandem.auth_service.api.dto.password_strength.common;

public enum PasswordStrength {

    BAD(0, 20),
    WEAK(21, 40),
    STANDARD(41, 60),
    GOOD(61, 80),
    STRONG(81, 100);

    private final int minScore;
    private final int maxScore;

    PasswordStrength(int minScore, int maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public static PasswordStrength fromScore(int score) {
        for (PasswordStrength strength : values()) {
            if (score >= strength.minScore && score <= strength.maxScore) {
                return strength;
            }
        }
        return BAD;
    }

    
}
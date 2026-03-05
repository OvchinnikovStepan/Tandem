package com.tandem.auth_service.utils;

import com.tandem.auth_service.api.dto.password.PasswordFeedback;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@UtilityClass
public class PasswordStrengthUtil {

    private static final Set<String> COMMON_PASSWORDS = Set.of(
            "password", "123456", "qwerty", "abc123"
    );

    public PasswordEvaluation evaluate(String password) {

        int score = 0;

        List<PasswordFeedback> feedback = new ArrayList<>();
        List<PasswordFeedback> warnings = new ArrayList<>();

        score += evaluateLength(password, feedback, warnings);
        score += evaluateVariety(password, feedback);
        score += evaluateCommon(password, warnings);
        score += evaluatePatterns(password, warnings);
        score += evaluateEntropy(password, feedback);

        score = Math.max(0, Math.min(score, 100));

        return new PasswordEvaluation(score, feedback, warnings);
    }

    private int evaluateLength(
            String password,
            List<PasswordFeedback> feedback,
            List<PasswordFeedback> warnings
    ) {

        int length = password.length();

        if (length < 8) {
            warnings.add(PasswordFeedback.TOO_SHORT);
            return 0;
        }

        if (length <= 11) return 10;
        if (length <= 15) return 15;
        return 20;
    }

    private int evaluateVariety(
            String password,
            List<PasswordFeedback> feedback
    ) {

        int score = 0;

        if (password.matches(".*[A-Z].*")) {
            feedback.add(PasswordFeedback.HAS_UPPERCASE);
            score += 15;
        } else {
            feedback.add(PasswordFeedback.MISSING_UPPERCASE);
        }

        if (password.matches(".*[a-z].*")) {
            feedback.add(PasswordFeedback.HAS_LOWERCASE);
            score += 15;
        } else {
            feedback.add(PasswordFeedback.MISSING_LOWERCASE);
        }

        if (password.matches(".*\\d.*")) {
            feedback.add(PasswordFeedback.HAS_NUMBER);
            score += 15;
        } else {
            feedback.add(PasswordFeedback.MISSING_NUMBER);
        }

        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};:,.<>?].*")) {
            feedback.add(PasswordFeedback.HAS_SPECIAL);
            score += 15;
        } else {
            feedback.add(PasswordFeedback.MISSING_SPECIAL);
        }

        return score;
    }

    private int evaluateCommon(
            String password,
            List<PasswordFeedback> warnings
    ) {

        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            warnings.add(PasswordFeedback.COMMON_PASSWORD);
            return -20;
        }

        return 10;
    }

    private int evaluatePatterns(
            String password,
            List<PasswordFeedback> warnings
    ) {

        int score = 0;

        if (hasSequential(password)) {
            warnings.add(PasswordFeedback.HAS_SEQUENTIAL);
            score -= 5;
        } else score += 5;

        if (hasRepeated(password)) {
            warnings.add(PasswordFeedback.HAS_REPEATED);
            score -= 5;
        } else score += 5;

        return score;
    }

    private int evaluateEntropy(
            String password,
            List<PasswordFeedback> feedback
    ) {

        long unique = password.chars().distinct().count();
        double ratio = (double) unique / password.length();

        if (ratio > 0.7) {
            feedback.add(PasswordFeedback.GOOD_ENTROPY);
            return 10;
        }

        feedback.add(PasswordFeedback.LOW_ENTROPY);
        return 0;
    }

    private boolean hasSequential(String password) {

        String seq = "abcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < seq.length() - 2; i++) {

            if (password.toLowerCase().contains(seq.substring(i, i + 3))) {
                return true;
            }
        }

        return false;
    }

    private boolean hasRepeated(String password) {

        return password.matches(".*(.)\\1{2,}.*");
    }

    public record PasswordEvaluation(
            int score,
            List<PasswordFeedback> feedback,
            List<PasswordFeedback> warnings
    ) {}
}
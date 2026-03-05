package com.tandem.auth_service.api;

import java.util.Locale;
import java.util.UUID;

final class AuthApiConfig {

    private AuthApiConfig() {
    }

    static String baseUrl() {
        String baseUrl = read("auth.api.base-url", "AUTH_API_BASE_URL", "http://localhost:8080/api/auth");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    static String defaultPassword() {
        return read("auth.api.default-password", "AUTH_API_DEFAULT_PASSWORD", "Qwerty123");
    }

    static String changedPassword() {
        return read("auth.api.changed-password", "AUTH_API_CHANGED_PASSWORD", "Qwerty456");
    }

    static String verificationCode() {
        return read("auth.api.verification-code", "AUTH_API_VERIFICATION_CODE", "123456");
    }

    static String randomEmail(String prefix) {
        return String.format(Locale.ROOT, "%s-%s@test.local", prefix, UUID.randomUUID());
    }

    static String randomPhone() {
        long randomPart = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return "+1" + randomPart;
    }

    private static String read(String propertyKey, String envKey, String defaultValue) {
        String fromProperty = System.getProperty(propertyKey);
        if (fromProperty != null && !fromProperty.isBlank()) {
            return fromProperty;
        }

        String fromEnv = System.getenv(envKey);
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv;
        }

        return defaultValue;
    }
}

package com.tandem.profile_service.api;

import java.util.Locale;
import java.util.UUID;

final class ProfileApiConfig {

    private ProfileApiConfig() {
    }

    static String baseUrl() {
        String baseUrl = read("profile.api.base-url", "PROFILE_API_BASE_URL", "http://45.130.147.42/api");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    static String authBaseUrl() {
        String baseUrl = read("auth.api.base-url", "AUTH_API_BASE_URL", "http://45.130.147.42/api/auth");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    static String defaultPassword() {
        return read("profile.api.default-password", "PROFILE_API_DEFAULT_PASSWORD", "Qwerty123");
    }

    static String verificationCode() {
        return read("profile.api.verification-code", "PROFILE_API_VERIFICATION_CODE", "123456");
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

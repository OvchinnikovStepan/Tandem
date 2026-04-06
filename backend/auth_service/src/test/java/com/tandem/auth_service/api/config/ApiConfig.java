package com.tandem.auth_service.api.config;

public final class ApiConfig {

    private ApiConfig() {
    }

    public static String baseUrl() {
        String baseUrl = read("auth.api.base-url", "AUTH_API_BASE_URL", "http://localhost:8080/api/auth");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    public static String defaultPassword() {
        return read("auth.api.default-password", "AUTH_API_DEFAULT_PASSWORD", "Qwerty123");
    }

    public static String changedPassword() {
        return read("auth.api.changed-password", "AUTH_API_CHANGED_PASSWORD", "Qwerty456");
    }

    public static String verificationCode() {
        return read("auth.api.verification-code", "AUTH_API_VERIFICATION_CODE", "123456");
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

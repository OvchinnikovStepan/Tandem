package com.tandem.interest_service.api.config;

/**
 * Точка чтения параметров для API-тестов.
 *
 * Каждое значение читается в порядке:
 *   1) Java system property
 *   2) Environment variable
 *   3) Default value (для локального запуска при поднятой через docker compose
 *      инфраструктуре auth_service + interest_service)
 */
public final class ApiConfig {

    private ApiConfig() {
    }

    /** Базовый URL interest_service (без хвостового слэша). */
    public static String baseUrl() {
        String baseUrl = read("interest.api.base-url", "INTEREST_API_BASE_URL",
                "http://localhost:8082/api");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    /** Базовый URL auth_service (используется для получения JWT). */
    public static String authBaseUrl() {
        String baseUrl = read("auth.api.base-url", "AUTH_API_BASE_URL",
                "http://localhost:8080/api/auth");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    /** Пароль по умолчанию при регистрации тестового пользователя. */
    public static String defaultPassword() {
        return read("interest.api.default-password", "INTEREST_API_DEFAULT_PASSWORD",
                "Qwerty123");
    }

    /** Захардкоженный код подтверждения телефона в auth_service. */
    public static String verificationCode() {
        return read("interest.api.verification-code", "INTEREST_API_VERIFICATION_CODE",
                "123456");
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

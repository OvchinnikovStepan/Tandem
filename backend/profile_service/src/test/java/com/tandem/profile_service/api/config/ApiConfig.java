package com.tandem.profile_service.api.config;

public final class ApiConfig {

    private ApiConfig() {
    }

    public static String baseUrl() {
        String baseUrl = read("profile.api.base-url", "PROFILE_API_BASE_URL", "http://localhost:8082/api");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    public static String authBaseUrl() {
        String baseUrl = read("auth.api.base-url", "AUTH_API_BASE_URL", "http://localhost:8080/api/auth");
        if (baseUrl.endsWith("/")) {
            return baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl;
    }

    public static String defaultPassword() {
        return read("profile.api.default-password", "PROFILE_API_DEFAULT_PASSWORD", "Qwerty123");
    }

    public static String verificationCode() {
        return read("profile.api.verification-code", "PROFILE_API_VERIFICATION_CODE", "123456");
    }

    public static String kafkaBootstrapServers() {
        return read("kafka.bootstrap-servers", "KAFKA_BOOTSTRAP_SERVERS", "localhost:9093");
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

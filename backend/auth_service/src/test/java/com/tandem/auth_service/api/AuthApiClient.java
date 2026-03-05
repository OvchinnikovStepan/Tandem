package com.tandem.auth_service.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

final class AuthApiClient {

    static {
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
    }

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    AuthApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .sslContext(trustAllSslContext())
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = AuthApiConfig.baseUrl();
    }

    private static SSLContext trustAllSslContext() {
        try {
            TrustManager[] trustAll = { new X509TrustManager() {
                @Override public void checkClientTrusted(X509Certificate[] chain, String authType) { }
                @Override public void checkServerTrusted(X509Certificate[] chain, String authType) { }
                @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }};
            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, trustAll, new SecureRandom());
            return ctx;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new IllegalStateException("Failed to create trust-all SSLContext", e);
        }
    }

    ApiResponse registerPhone(String phoneNumber) {
        return send("POST", "/register/phone", Map.of("phoneNumber", phoneNumber), null);
    }

    ApiResponse registerVerify(String verificationId, String code) {
        return send("POST", "/register/verify", Map.of("verificationId", verificationId, "code", code), null);
    }

    ApiResponse registerEmail(String verificationId, String email, String password) {
        return send("POST", "/register/email",
                Map.of("verificationId", verificationId, "email", email, "password", password), null);
    }


    ApiResponse registerFull(String phone, String email, String password) {
        ApiResponse phoneResp = registerPhone(phone);
        String verificationId = phoneResp.body().path("verificationId").asText("");
        if (verificationId.isBlank()) return phoneResp;

        ApiResponse verifyResp = registerVerify(verificationId, AuthApiConfig.verificationCode());
        if (verifyResp.statusCode() != 200) return verifyResp;

        return registerEmail(verificationId, email, password);
    }

    ApiResponse login(String email, String password) {
        return send("POST", "/login", Map.of("email", email, "password", password), null);
    }

    ApiResponse refresh(String refreshToken) {
        return send("POST", "/refresh", Map.of("refreshToken", refreshToken), null);
    }

    ApiResponse me(String accessToken) {
        return send("GET", "/me", null, accessToken);
    }

    ApiResponse logout(String accessToken) {
        return send("POST", "/logout", null, accessToken);
    }

    ApiResponse checkPasswordStrength(String password) {
        return send("POST", "/password/check-strength", Map.of("password", password), null);
    }

    ApiResponse passwordResetRequest(String identifier, String method) {
        return send("POST", "/password/reset/request", Map.of("identifier", identifier, "method", method), null);
    }

    ApiResponse passwordResetVerify(String resetToken) {
        return send("POST", "/password/reset/verify", Map.of("resetToken", resetToken), null);
    }

    ApiResponse passwordResetComplete(String resetToken, String newPassword) {
        return send("POST", "/password/reset/complete", Map.of("resetToken", resetToken, "newPassword", newPassword), null);
    }

    ApiResponse sessions(String accessToken) {
        return send("GET", "/sessions", null, accessToken);
    }

    ApiResponse deleteSession(String sessionId, String accessToken) {
        return send("DELETE", "/sessions/" + sessionId, null, accessToken);
    }

    ApiResponse send(String method, String path, Object requestBody, String bearerToken) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(Duration.ofSeconds(20));

            if (bearerToken != null && !bearerToken.isBlank()) {
                requestBuilder.header("Authorization", "Bearer " + bearerToken);
            }

            if (requestBody != null) {
                requestBuilder.header("Content-Type", "application/json");
                requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)));
            } else {
                requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            System.out.printf("[DEBUG] %s %s%s -> %d | %s%n", method, baseUrl, path, response.statusCode(), response.body());
            JsonNode body = parseBody(response.body());
            return new ApiResponse(response.statusCode(), body);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Request interrupted", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Request failed", ex);
        }
    }

    private JsonNode parseBody(String body) {
        if (body == null || body.isBlank()) {
            return MissingNode.getInstance();
        }

        try {
            return objectMapper.readTree(body);
        } catch (IOException ex) {
            return MissingNode.getInstance();
        }
    }

    record ApiResponse(int statusCode, JsonNode body) {
    }
}

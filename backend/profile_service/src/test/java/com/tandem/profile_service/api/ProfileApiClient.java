package com.tandem.profile_service.api;

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

final class ProfileApiClient {

    static {
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
    }

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String authBaseUrl;

    ProfileApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .sslContext(trustAllSslContext())
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = ProfileApiConfig.baseUrl();
        this.authBaseUrl = ProfileApiConfig.authBaseUrl();
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

    ApiResponse authRegisterPhone(String phoneNumber) {
        return sendAuth("POST", "/register/phone", Map.of("phoneNumber", phoneNumber), null);
    }

    ApiResponse authRegisterVerify(String verificationId, String code) {
        return sendAuth("POST", "/register/verify", Map.of("verificationId", verificationId, "code", code), null);
    }

    ApiResponse authRegisterEmail(String verificationId, String email, String password) {
        return sendAuth("POST", "/register/email",
                Map.of("verificationId", verificationId, "email", email, "password", password), null);
    }

    ApiResponse authLogin(String email, String password) {
        return sendAuth("POST", "/login", Map.of("email", email, "password", password), null);
    }


    String obtainAccessToken(String phone, String email, String password) {
        ApiResponse phoneResp = authRegisterPhone(phone);
        String verificationId = phoneResp.body().path("verificationId").asText("");
        if (verificationId.isBlank()) return null;

        authRegisterVerify(verificationId, ProfileApiConfig.verificationCode());

        ApiResponse regEmail = authRegisterEmail(verificationId, email, password);
        String token = regEmail.body().path("accessToken").asText("");
        if (!token.isBlank()) return token;

        ApiResponse login = authLogin(email, password);
        return login.body().path("accessToken").asText(null);
    }


    ApiResponse getAllProfiles(String accessToken) {
        return send("GET", "/profiles", null, accessToken);
    }

    ApiResponse getMyProfile(String accessToken) {
        return send("GET", "/profile/me", null, accessToken);
    }

    ApiResponse deleteMyProfile(String accessToken) {
        return send("DELETE", "/profile/me", null, accessToken);
    }

    ApiResponse getProfileById(String userId, String accessToken) {
        return send("GET", "/profile/" + userId, null, accessToken);
    }

    ApiResponse updateMyProfile(Object body, String accessToken) {
        return send("PUT", "/profile/me", body, accessToken);
    }

    ApiResponse patchMyProfile(Object body, String accessToken) {
        return send("PATCH", "/profile/me", body, accessToken);
    }

    ApiResponse getMyPrivacySettings(String accessToken) {
        return send("GET", "/profile/me/privacy", null, accessToken);
    }

    ApiResponse updateMyPrivacySettings(Object body, String accessToken) {
        return send("PUT", "/profile/me/privacy", body, accessToken);
    }

    ApiResponse getOnboardingQuestions(String accessToken) {
        return send("GET", "/profile/onboarding/questions", null, accessToken);
    }

    ApiResponse completeOnboarding(Object body, String accessToken) {
        return send("POST", "/profile/onboarding/complete", body, accessToken);
    }

    ApiResponse send(String method, String path, Object requestBody, String bearerToken) {
        return doSend(method, baseUrl + path, requestBody, bearerToken);
    }

    private ApiResponse sendAuth(String method, String path, Object requestBody, String bearerToken) {
        return doSend(method, authBaseUrl + path, requestBody, bearerToken);
    }

    private ApiResponse doSend(String method, String url, Object requestBody, String bearerToken) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20));

            if (bearerToken != null && !bearerToken.isBlank()) {
                requestBuilder.header("Authorization", "Bearer " + bearerToken);
            }

            if (requestBody != null) {
                requestBuilder.header("Content-Type", "application/json");
                requestBuilder.method(method,
                        HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)));
            } else {
                requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(),
                    HttpResponse.BodyHandlers.ofString());
            System.out.printf("[DEBUG] %s %s -> %d | %s%n", method, url, response.statusCode(), response.body());
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

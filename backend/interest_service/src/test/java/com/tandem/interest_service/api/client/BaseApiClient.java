package com.tandem.interest_service.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.tandem.interest_service.api.config.ApiConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Низкоуровневый HTTP-клиент: отправляет запросы и парсит ответы в JsonNode.
 * Используется и из *Client классов API-тестов, и напрямую из ApiProbeTest.
 */
public class BaseApiClient {

    static {
        System.setProperty("jdk.internal.httpclient.disableHostnameVerification", "true");
    }

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public BaseApiClient() {
        this(ApiConfig.baseUrl());
    }

    public BaseApiClient(String baseUrl) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .sslContext(trustAllSslContext())
                .build();
        this.objectMapper = new ObjectMapper();
        this.baseUrl = baseUrl;
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

    public ApiResponse send(String method, String path, Object requestBody, String bearerToken) {
        try {
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
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

            System.out.printf("[DEBUG] %s %s%s -> %d | %s%n",
                    method, baseUrl, path, response.statusCode(), response.body());

            JsonNode body = parseBody(response.body());
            return new ApiResponse(response.statusCode(), body, response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Request interrupted", ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Request failed", ex);
        }
    }

    /** Удобный шорткат для GET с query-параметрами. */
    public ApiResponse get(String path, Map<String, Object> queryParams, String bearerToken) {
        if (queryParams == null || queryParams.isEmpty()) {
            return send("GET", path, null, bearerToken);
        }
        StringBuilder sb = new StringBuilder(path);
        sb.append(path.contains("?") ? '&' : '?');
        boolean first = true;
        for (Map.Entry<String, Object> e : new LinkedHashMap<>(queryParams).entrySet()) {
            if (e.getValue() == null) continue;
            if (!first) sb.append('&');
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8));
            first = false;
        }
        return send("GET", sb.toString(), null, bearerToken);
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

    /**
     * @param statusCode HTTP-статус
     * @param body       тело, распаршенное как JsonNode (или MissingNode, если не JSON)
     * @param rawBody    исходное строковое тело (нужно для plain-text ответов вроде сообщений об удалении)
     */
    public record ApiResponse(int statusCode, JsonNode body, String rawBody) {
    }
}

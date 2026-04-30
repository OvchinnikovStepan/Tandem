package com.tandem.notification_service.api.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RequestUserProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String USER_ID_HEADER = "X-User-Id";

    private final ObjectMapper objectMapper;

    public UUID getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalArgumentException("Unable to resolve request context");
        }

        HttpServletRequest request = attributes.getRequest();
        UUID userIdFromBearer = resolveFromAuthorizationHeader(request);
        if (userIdFromBearer != null) {
            return userIdFromBearer;
        }

        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (userIdHeader == null || userIdHeader.isBlank()) {
            throw new IllegalArgumentException("Authorization Bearer token or X-User-Id header is required");
        }

        return UUID.fromString(userIdHeader);
    }

    private UUID resolveFromAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return null;
        }
        String token = authorization.substring("Bearer ".length()).trim();
        if (token.isBlank()) {
            return null;
        }

        UUID directUuid = tryParseUuid(token);
        if (directUuid != null) {
            return directUuid;
        }

        return resolveFromJwtSub(token);
    }

    private UUID resolveFromJwtSub(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            return null;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(new String(decoded, StandardCharsets.UTF_8));
            JsonNode subNode = payload.get("sub");
            if (subNode == null || subNode.asText().isBlank()) {
                return null;
            }
            return tryParseUuid(subNode.asText());
        } catch (Exception ignored) {
            return null;
        }
    }

    private UUID tryParseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception ignored) {
            return null;
        }
    }
}

package com.tandem.profile_service.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            UUID userId = extractUserId(token);

            if (userId == null) {
                filterChain.doFilter(request, response);
                return;
            }

            AuthPrincipal principal = new AuthPrincipal(userId, null);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    List.of()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user: {}", userId);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает userId из JWT токена
     */
    public UUID extractUserId(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }

            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JsonNode jsonNode = objectMapper.readTree(payloadJson);

            String userIdStr = jsonNode.get("sub") == null ? null : jsonNode.get("sub").asText();

            if (userIdStr == null || userIdStr.isEmpty()) {
                return null;
            }

            return UUID.fromString(userIdStr);

        } catch (Exception e) {
            log.debug("Error parsing JWT: {}", e.getMessage());
            return null;
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/v3/api-docs") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/swagger-resources");
    }
}
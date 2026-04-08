package com.tandem.interest_service.security;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@UtilityClass
@Slf4j
public class SecurityUtils {

    /**
     * Извлекает userId текущего аутентифицированного пользователя
     * @return UUID пользователя или null если не аутентифицирован
     */
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthPrincipal) {
            return ((AuthPrincipal) principal).userId();
        }

        log.warn("Unexpected principal type: {}",
                principal != null ? principal.getClass().getName() : "null");
        return null;
    }

    /**
     * Извлекает userId или выбрасывает исключение. Используется в контроллере
     */
    public UUID getCurrentUserIdOrThrow() {
        UUID userId = getCurrentUserId();
        if (userId == null) {
            throw new org.springframework.security.authentication
                    .AuthenticationCredentialsNotFoundException("User not authenticated");
        }
        return userId;
    }
}
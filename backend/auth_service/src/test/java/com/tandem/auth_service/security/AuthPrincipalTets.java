package com.tandem.auth_service.security;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthPrincipalTest {

    @Test
    void shouldStoreValuesCorrectly() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        AuthPrincipal principal = new AuthPrincipal(userId, sessionId);

        assertEquals(userId, principal.userId());
        assertEquals(sessionId, principal.sessionId());
    }
}

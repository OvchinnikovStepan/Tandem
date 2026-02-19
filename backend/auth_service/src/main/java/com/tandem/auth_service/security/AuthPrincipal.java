package com.tandem.auth_service.security;

import java.util.UUID;

public record AuthPrincipal(
        UUID userId,
        UUID sessionId
) {}

package com.tandem.interest_service.security;

import java.util.UUID;

public record AuthPrincipal(
        UUID userId,
        UUID sessionId
) {}
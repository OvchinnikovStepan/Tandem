package com.tandem.profile_service.security;

import java.util.UUID;

public record AuthPrincipal(
        UUID userId,
        UUID sessionId
) {}
package com.tandem.auth_service.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpExtractor {
    public String extractIp(HttpServletRequest request) {
    String xff = request.getHeader("X-Forwarded-For");
    if (xff != null && !xff.isBlank()) {
        return xff.split(",")[0].trim();
    }
    return request.getRemoteAddr();
    }
}

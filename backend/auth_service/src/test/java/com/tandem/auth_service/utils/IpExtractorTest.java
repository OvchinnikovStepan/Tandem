package com.tandem.auth_service.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class IpExtractorTest {

    private final IpExtractor extractor = new IpExtractor();

    @Test
    void shouldExtractIpFromXForwardedFor() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Forwarded-For"))
                .thenReturn("192.168.0.1, 10.0.0.1");

        String ip = extractor.extractIp(request);

        assertThat(ip).isEqualTo("192.168.0.1");
    }

    @Test
    void shouldFallbackToRemoteAddr() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getHeader("X-Forwarded-For"))
                .thenReturn(null);

        when(request.getRemoteAddr())
                .thenReturn("127.0.0.1");

        String ip = extractor.extractIp(request);

        assertThat(ip).isEqualTo("127.0.0.1");
    }
}

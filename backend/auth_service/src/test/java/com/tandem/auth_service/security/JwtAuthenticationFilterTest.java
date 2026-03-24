package com.tandem.auth_service.security;

import com.tandem.auth_service.service.session.SessionService;
import com.tandem.auth_service.service.token.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private SessionService sessionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipWhenNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSkipWhenHeaderNotBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic 123");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldSkipWhenTokenInvalid() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.validateToken("token")).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldAuthenticateWhenTokenValidAndSessionActive() throws Exception {
        String token = "validToken";
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(userId);
        when(jwtService.extractSessionId(token)).thenReturn(sessionId);
        when(sessionService.isSessionActive(sessionId)).thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        AuthPrincipal principal =
                (AuthPrincipal) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        assertEquals(userId, principal.userId());
        assertEquals(sessionId, principal.sessionId());
    }

    @Test
    void shouldClearContextWhenSessionNotActive() throws Exception {
        String token = "token";
        UUID sessionId = UUID.randomUUID();

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUserId(token)).thenReturn(UUID.randomUUID());
        when(jwtService.extractSessionId(token)).thenReturn(sessionId);
        when(sessionService.isSessionActive(sessionId)).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(chain, atLeastOnce()).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldHandleExceptionGracefully() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtService.validateToken("token")).thenReturn(true);
        when(jwtService.extractUserId("token")).thenThrow(new RuntimeException());

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}

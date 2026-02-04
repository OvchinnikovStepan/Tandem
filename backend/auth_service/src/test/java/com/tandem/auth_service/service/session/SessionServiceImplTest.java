package com.tandem.auth_service.service.session;

import com.tandem.auth_service.api.dto.RefreshResultDto;
import com.tandem.auth_service.api.dto.SessionDto;
import com.tandem.auth_service.model.Session;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.SessionRepository;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.token.JwtService;
import com.tandem.auth_service.service.token.TokenHashUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceImplTest {

    @Mock
    SessionRepository sessionRepository;

    @Mock
    JwtService jwtService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    SessionServiceImpl sessionService;

    @Test
    void createSession_shouldSaveSessionWithHashedTokens() {
        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        String accessToken = "access";
        String refreshToken = "refresh";

        sessionService.createSession(
                sessionId,
                userId,
                accessToken,
                refreshToken,
                "127.0.0.1",
                "chrome"
        );

        ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);

        verify(sessionRepository).save(captor.capture());

        Session saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(sessionId);
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(saved.getDeviceInfo()).isEqualTo("chrome");

        assertThat(saved.getAccessTokenHash()).isNotEqualTo(accessToken);
        assertThat(saved.getRefreshTokenHash()).isNotEqualTo(refreshToken);

        assertThat(saved.getExpiresAt())
                .isAfter(LocalDateTime.now().plusDays(29));
        }

    @Test
    void logout_shouldRevokeSession() {
        UUID sessionId = UUID.randomUUID();

        sessionService.logout(sessionId);

        verify(sessionRepository).revoke(eq(sessionId), any(LocalDateTime.class));
        }

        @Test
        void refresh_shouldReturnNewTokens_whenValidSession() {
        String refreshToken = "refresh-token";
        String refreshHash = TokenHashUtil.sha256(refreshToken);

        UUID sessionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Session session = Session.builder()
                .id(sessionId)
                .userId(userId)
                .refreshTokenHash(refreshHash)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revokedAt(null)
                .build();

        User user = User.builder()
                .id(userId)
                .build();

        when(sessionRepository.findByRefreshTokenHash(refreshHash))
                .thenReturn(Optional.of(session));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(jwtService.generateAccessToken(userId, sessionId))
                .thenReturn("newAccessToken");

        RefreshResultDto result = sessionService.refresh(refreshToken);

        assertThat(result.accessToken()).isEqualTo("newAccessToken");
        assertThat(result.refreshToken()).isNotBlank();

        verify(sessionRepository).updateTokensHash(
                eq(sessionId),
                anyString(),
                anyString()
        );
    }

    @Test
    void refresh_shouldThrowException_whenTokenNotFound() {
        when(sessionRepository.findByRefreshTokenHash(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                sessionService.refresh("bad")
        ).isInstanceOf(IllegalStateException.class);
    }


    @Test
    void refresh_shouldThrowException_whenSessionExpired() {
        String refreshToken = "token";
        String hash = TokenHashUtil.sha256(refreshToken);

        Session expiredSession = Session.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .refreshTokenHash(hash)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        when(sessionRepository.findByRefreshTokenHash(hash))
                .thenReturn(Optional.of(expiredSession));

        assertThatThrownBy(() ->
                sessionService.refresh(refreshToken)
        ).isInstanceOf(IllegalStateException.class);
        }

    @Test
    void getActiveSessions_shouldReturnDtoList() {
        UUID userId = UUID.randomUUID();

        Session session = Session.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .deviceInfo("chrome")
                .ipAddress("127.0.0.1")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        when(sessionRepository.findActiveByUserId(userId))
                .thenReturn(List.of(session));

        List<SessionDto> result = sessionService.getActiveSessions(userId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).deviceInfo()).isEqualTo("chrome");
    }

    @Test
    void terminateSession_shouldRevokeSession() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        sessionService.terminateSession(userId, sessionId);

        verify(sessionRepository).revoke(eq(sessionId), any(LocalDateTime.class));
    }

    @Test
    void isSessionActive_shouldReturnTrue_whenActive() {
        UUID sessionId = UUID.randomUUID();

        Session session = Session.builder()
                .id(sessionId)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revokedAt(null)
                .build();

        when(sessionRepository.findSessionById(sessionId))
                .thenReturn(session);

        Boolean result = sessionService.isSessionActive(sessionId);

        assertThat(result).isTrue();
    }


    @Test
    void isSessionActive_shouldReturnFalse_whenRevoked() {
        UUID sessionId = UUID.randomUUID();

        Session session = Session.builder()
                .id(sessionId)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revokedAt(LocalDateTime.now())
                .build();

        when(sessionRepository.findSessionById(sessionId))
                .thenReturn(session);

        Boolean result = sessionService.isSessionActive(sessionId);

        assertThat(result).isFalse();
    }

    @Test
    void isSessionActive_shouldReturnFalse_whenExpired() {
        UUID sessionId = UUID.randomUUID();

        Session session = Session.builder()
                .id(sessionId)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .revokedAt(null)
                .build();

        when(sessionRepository.findSessionById(sessionId))
                .thenReturn(session);

        Boolean result = sessionService.isSessionActive(sessionId);

        assertThat(result).isFalse();
    }

    @Test
    void cleanupExpiredSessions_shouldCallRepository() {
        when(sessionRepository.deleteExpired()).thenReturn(5);

        sessionService.cleanupExpiredSessions();

        verify(sessionRepository).deleteExpired();
    }

}

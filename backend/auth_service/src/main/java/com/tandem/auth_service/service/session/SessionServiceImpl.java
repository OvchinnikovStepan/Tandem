package com.tandem.auth_service.service.session;

import com.tandem.auth_service.api.dto.RefreshResultDto;
import com.tandem.auth_service.api.dto.SessionDto;
import com.tandem.auth_service.model.Session;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.SessionRepository;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.token.JwtService;
import com.tandem.auth_service.utils.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;


    @Override
    public void createSession(
            UUID sessionId,
            UUID userId,
            String accessToken,
            String refreshToken,
            String ipAddress,
            String deviceInfo
    ) {
        
        Session session = Session.builder()
            .id(sessionId)
            .userId(userId)
            .accessTokenHash(TokenHashUtil.sha256(accessToken))
            .refreshTokenHash(TokenHashUtil.sha256(refreshToken))
            .deviceInfo(deviceInfo)
            .ipAddress(ipAddress)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(30))
            .revokedAt(null)
            .build();


        sessionRepository.save(session);
    }

    @Override
    public void logout(UUID sessionId) {
        sessionRepository.revoke(sessionId, LocalDateTime.now());
    }

    @Override
    public RefreshResultDto refresh(String refreshToken) {

        String hash = TokenHashUtil.sha256(refreshToken);

        Session session = sessionRepository.findByRefreshTokenHash(hash)
            .filter(s -> s.getRevokedAt() == null)
            .filter(s -> s.getExpiresAt().isAfter(LocalDateTime.now()))
            .orElseThrow(() -> new IllegalStateException("Invalid refresh token"));

        User user = userRepository.findById(session.getUserId())
            .orElseThrow();

        String newAccessToken = jwtService.generateAccessToken(
            user.getId(),
            session.getId()
        );

        String newRefreshToken = UUID.randomUUID().toString();

        sessionRepository.updateTokensHash(
            session.getId(),
            TokenHashUtil.sha256(newAccessToken),
            TokenHashUtil.sha256(newRefreshToken)
        );

        RefreshResultDto refreshResult=new RefreshResultDto(newAccessToken,newRefreshToken);
        return refreshResult;
    }

    @Override
    public List<SessionDto> getActiveSessions(UUID userId) {
        return sessionRepository.findActiveByUserId(userId)
            .stream()
            .map(Session::toDto)
            .toList();
    }

    @Override
    public void terminateSession(UUID userId, UUID sessionId) {
        sessionRepository.revoke(sessionId, LocalDateTime.now());
    }

    @Override
    public Boolean isSessionActive(UUID sessionId) {
       Session session = sessionRepository.findSessionById(sessionId);

       return ((session.getRevokedAt()==null) &&
               (session.getExpiresAt().isAfter(LocalDateTime.now())));
    }

    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredSessions() {
        int deleted = sessionRepository.deleteExpired();
        log.info("Session cleanup: {} sessions removed", deleted);
    }

}

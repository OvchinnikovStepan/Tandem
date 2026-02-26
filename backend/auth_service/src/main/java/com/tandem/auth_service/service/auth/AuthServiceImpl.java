package com.tandem.auth_service.service.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tandem.auth_service.api.dto.UserDto;
import com.tandem.auth_service.api.dto.response.LoginResponse;
import com.tandem.auth_service.kafka.UserEventPublisher;
import com.tandem.auth_service.kafka.events.UserLoggedInEvent;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.session.SessionService;
import com.tandem.auth_service.service.token.JwtService;
import com.tandem.auth_service.utils.IpExtractor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionService sessionService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final IpExtractor ipExtractor;
    private final UserEventPublisher userEventPublisher;

    @Override
    public LoginResponse login(HttpServletRequest httpRequest, String email, String rawPassword) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Invalid credentials"));

        if (user.getPasswordHash() == null ||
            !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalStateException("Invalid credentials");
        }

        UUID sessionId = UUID.randomUUID();

        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                sessionId
        );

        String refreshToken = UUID.randomUUID().toString();
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.update(user);

        String ipAddress = ipExtractor.extractIp(httpRequest);
        String deviceInfo = httpRequest.getHeader("User-Agent");

        sessionService.createSession(
            sessionId,
            user.getId(),
            accessToken,
            refreshToken,
            ipAddress,
            deviceInfo
        );

        userEventPublisher.publishUserLogin(
                UserLoggedInEvent.of(
                        user.getId(),
                        ipAddress,
                        deviceInfo
                )
        );

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public UserDto getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.getCreatedAt(),
                user.getLastLoginAt()
        );
    }
}

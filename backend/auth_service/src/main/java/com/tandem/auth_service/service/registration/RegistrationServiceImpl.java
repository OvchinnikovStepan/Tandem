package com.tandem.auth_service.service.registration;

import com.tandem.auth_service.api.dto.PasswordStrength;
import com.tandem.auth_service.api.dto.response.RegisterEmailResponse;
import com.tandem.auth_service.api.error.exceptions.VerificationCodeInvalidException;
import com.tandem.auth_service.kafka.UserEventPublisher;
import com.tandem.auth_service.kafka.events.UserRegisteredEvent;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.session.SessionService;
import com.tandem.auth_service.service.token.JwtService;
import com.tandem.auth_service.utils.IpExtractor;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.password.PasswordEncoder;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordStrengthService passwordStrengthService;
    private final IpExtractor ipExtractor;
    private final SessionService sessionService;
    private final UserEventPublisher userEventPublisher;


    @Override
    public UUID startPhoneRegistration(String phoneNumber) {

        userRepository.findByPhoneNumber(phoneNumber)
                .ifPresent(u -> {
                    throw new IllegalStateException("Phone already registered");
                });

            UUID userId = UUID.randomUUID();

            User user = User.builder()
                .id(userId)
                .phoneNumber(phoneNumber)
                .email(phoneNumber + "@temp.local")
                .passwordHash(null)
                .phoneVerified(false)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

            userRepository.save(user);

            // SMS + Redis (пока заглушка)
            // redis.save("verify:" + userId, "123456", 5 min)

            return userId;
    }

    @Override
    public void verifyPhone(UUID verificationId, String code) {

            // String expected = redis.get("verify:" + verificationId);

            if (!"123456".equals(code)) { //Пока заглушечка
                throw new VerificationCodeInvalidException("Invalid verification code");
            }

            User user = userRepository.findById(verificationId)
                .orElseThrow(() -> new VerificationCodeInvalidException("Invalid verification code"));

            user.setPhoneVerified(true);
            userRepository.update(user);
        }

        @Override
        public RegisterEmailResponse completeRegistration(
                HttpServletRequest httpRequest,
                UUID userId,
                String email,
                String rawPassword
        ) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User not found"));

            if (!user.isPhoneVerified()) {
                throw new IllegalStateException("Phone not verified");
            }

            if (user.isEmailVerified()) {
                throw new IllegalStateException("User already registered");
            }


            user.setEmail(email);
            user.setPasswordHash(passwordEncoder.encode(rawPassword));
            user.setEmailVerified(true);
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.update(user);

            UUID sessionId = UUID.randomUUID();

            String accessToken = jwtService.generateAccessToken(
                    user.getId(),
                    sessionId
            );

            String refreshToken = UUID.randomUUID().toString();

            PasswordStrength strength = passwordStrengthService.evaluate(rawPassword);

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

            userEventPublisher.publishUserRegistered(
                    UserRegisteredEvent.of(
                            user.getId(),
                            user.getEmail(),
                            user.getPhoneNumber()
                    )
            );

            return new RegisterEmailResponse(
                    user.getId(),
                    accessToken,
                    refreshToken,
                    strength
            );
}
}

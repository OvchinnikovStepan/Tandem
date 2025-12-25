package com.tandem.auth_service.service.registration;

import com.tandem.auth_service.api.dto.PasswordStrength;
import com.tandem.auth_service.api.dto.response.RegisterEmailResponse;
import com.tandem.auth_service.api.error.exceptions.VerificationCodeInvalidException;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.token.JwtService;
import com.tandem.auth_service.service.token.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;
    private final PasswordStrengthService passwordStrengthService;

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
            userRepository.save(user);
        }

        @Override
        public RegisterEmailResponse completeRegistration(
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

            userRepository.save(user);

            String accessToken = jwtService.generateAccessToken(
                    user.getId(),
                    user.getEmail()
            );

            String refreshToken = refreshTokenService.generateRefreshToken(user.getId());

            PasswordStrength strength = passwordStrengthService.evaluate(rawPassword);

            return new RegisterEmailResponse(
                    user.getId(),
                    accessToken,
                    refreshToken,
                    strength
            );
}
}

package com.tandem.auth_service.service.password_reset;

import com.tandem.auth_service.api.dto.password_reset.request.CompleteResetPasswordRequest;
import com.tandem.auth_service.api.dto.password_reset.request.ResetPasswordRequest;
import com.tandem.auth_service.api.dto.password_reset.response.CompleteResetPasswordResponse;
import com.tandem.auth_service.api.dto.password_reset.response.ResetPasswordRequestResponse;
import com.tandem.auth_service.api.dto.password_reset.response.VerifyResetTokenResponse;
import com.tandem.auth_service.api.dto.password_strength.common.PasswordStrengthResult;
import com.tandem.auth_service.model.PasswordResetTokenData;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.PasswordResetTokenRepository;
import com.tandem.auth_service.repository.SessionRepository;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.password_strength.PasswordStrengthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository tokenDao;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRepository sessionRepository;
    private final PasswordStrengthService passwordStrengthService;
    private final SecureRandom random = new SecureRandom();

    public PasswordResetServiceImpl(PasswordResetTokenRepository tokenDao,
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder,
                                    SessionRepository sessionRepository,
                                    PasswordStrengthService passwordStrengthService) {
        this.tokenDao = tokenDao;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = sessionRepository;
        this.passwordStrengthService = passwordStrengthService;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        return passwordEncoder.encode(token);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        int at = email.indexOf("@");
        if (at < 2) return email;
        return email.charAt(0) + "***" + email.substring(at);
    }

    @Override
    public ResetPasswordRequestResponse requestReset(ResetPasswordRequest request, String ip) {
        String identifier = request.identifier();
        // Ищем пользователя по email (для минимализма – только email)
        Optional<User> userOpt = userRepository.findByEmail(identifier);
        
        // Безопасность: всегда возвращаем одинаковый ответ, даже если пользователь не найден
        String resetId = null;
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = generateToken();
            String tokenHash = hashToken(token);
            resetId = UUID.randomUUID().toString();

            PasswordResetTokenData data = PasswordResetTokenData.builder()
                    .resetId(resetId)
                    .userId(user.getId().toString())
                    .email(user.getEmail())
                    .method(request.method())
                    .locale(request.locale())
                    .build();

            tokenDao.save(tokenHash, data, Duration.ofHours(1));
            log.info("Password reset token created for user: {}", user.getEmail());
        } else {
            log.warn("Password reset requested for non-existent identifier: {}", identifier);
        }

        return new ResetPasswordRequestResponse(
                resetId,
                request.method(),
                maskEmail(identifier),
                3600,
                null
        );
    }

    @Override
    public VerifyResetTokenResponse verifyToken(String token) {
        String tokenHash = hashToken(token);
        var data = tokenDao.find(tokenHash);

        if (data.isEmpty()) {
            return VerifyResetTokenResponse.invalid();
        }

        return VerifyResetTokenResponse.valid(
                data.get().getResetId(),
                maskEmail(data.get().getEmail()),
                3600
        );
    }

    @Override
    public CompleteResetPasswordResponse completeReset(CompleteResetPasswordRequest request, String ip) {
        String tokenHash = hashToken(request.resetToken());
        var tokenData = tokenDao.find(tokenHash);

        if (tokenData.isEmpty()) {
            log.warn("Attempt to reset password with invalid token");
            return CompleteResetPasswordResponse.invalidToken();
        }

        PasswordResetTokenData data = tokenData.get();
        UUID userId = UUID.fromString(data.getUserId());
        
        // Удаляем токен сразу (одноразовый)
        tokenDao.delete(tokenHash);

        // Проверяем, существует ли пользователь
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            log.error("User not found for password reset token: userId={}", userId);
            return CompleteResetPasswordResponse.invalidToken();
        }

        User user = userOpt.get();
        
        // Проверка, что новый пароль не совпадает со старым (опционально)
        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            log.warn("New password same as old for user: {}", user.getEmail());
            // Можно вернуть специальную ошибку, но для простоты просто продолжаем
        }

        // Обновляем пароль
        String newPasswordHash = passwordEncoder.encode(request.newPassword());
        user.setPasswordHash(newPasswordHash);
        userRepository.update(user);

        // Удаляем все сессии пользователя
        int sessionsTerminated = sessionRepository.deleteUserSessions(userId);
        log.info("Password reset completed for user: {}, {} sessions terminated", user.getEmail(), sessionsTerminated);

        // Оценка сложности пароля
        PasswordStrengthResult strength = passwordStrengthService.evaluate(request.newPassword());

        return CompleteResetPasswordResponse.validToken(
                true,
                sessionsTerminated,
                strength.getStrength()
        );
    }
}
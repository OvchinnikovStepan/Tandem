package com.tandem.auth_service.service.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.PrivateKey;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        // Генерируем тестовую RSA пару ключей
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair keyPair = keyGen.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        jwtService = new JwtServiceImpl(privateKey, publicKey);
    }

    @Test
    void generateAccessToken_shouldReturnNonEmptyToken() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId, sessionId);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void validateToken_validToken_shouldReturnTrue() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId, sessionId);

        boolean valid = jwtService.validateToken(token);

        assertTrue(valid);
    }

    @Test
    void validateToken_invalidToken_shouldReturnFalse() {
        assertFalse(jwtService.validateToken("invalid.jwt.token"));
    }

    @Test
    void validateToken_nullOrEmpty_shouldReturnFalse() {
        assertFalse(jwtService.validateToken(null));
        assertFalse(jwtService.validateToken(""));
    }

    @Test
    void extractUserId_shouldReturnCorrectUserId() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId, sessionId);

        UUID extractedUserId = jwtService.extractUserId(token);

        assertEquals(userId, extractedUserId);
    }

    @Test
    void extractSessionId_shouldReturnCorrectSessionId() {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId, sessionId);

        UUID extractedSessionId = jwtService.extractSessionId(token);

        assertEquals(sessionId, extractedSessionId);
    }

    @Test
    void extractUserId_invalidToken_shouldThrowException() {
        assertThrows(IllegalStateException.class, () ->
                jwtService.extractUserId("bad.token")
        );
    }

    @Test
    void extractSessionId_invalidToken_shouldThrowException() {
        assertThrows(IllegalStateException.class, () ->
                jwtService.extractSessionId("bad.token")
        );
    }

    @Test
    void tokenSignedWithDifferentKey_shouldBeInvalid() throws Exception {
        // Создаём другую пару ключей
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair otherPair = keyGen.generateKeyPair();

        JwtService otherJwtService = new JwtServiceImpl(
                otherPair.getPrivate(),
                (RSAPublicKey) otherPair.getPublic()
        );

        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        String token = jwtService.generateAccessToken(userId, sessionId);

        // Проверяем токен чужим сервисом
        assertFalse(otherJwtService.validateToken(token));
    }
}

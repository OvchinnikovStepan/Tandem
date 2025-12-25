package com.tandem.auth_service.service.token;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtServiceImpl implements JwtService {

    private final RSAPublicKey publicKey;
    private final JWSSigner signer;

    public JwtServiceImpl(PrivateKey privateKey, RSAPublicKey publicKey) {
        this.signer = new RSASSASigner(privateKey);
        this.publicKey = publicKey;
    }

    @Override
    public String generateAccessToken(UUID userId, String email) {
        try {
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(userId.toString())
                    .claim("email", email)
                    .claim("type", "access")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                    .build();

            SignedJWT jwt = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.RS256),
                    claims
            );

            jwt.sign(signer);
            return jwt.serialize();

        } catch (JOSEException e) {
            throw new IllegalStateException("JWT generation failed", e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return jwt.verify(new RSASSAVerifier(publicKey))
                    && jwt.getJWTClaimsSet().getExpirationTime().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public UUID extractUserId(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            return UUID.fromString(jwt.getJWTClaimsSet().getSubject());
        } catch (Exception e) {
            throw new IllegalStateException("Invalid token", e);
        }
    }
}

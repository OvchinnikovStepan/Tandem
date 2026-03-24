package com.tandem.auth_service.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TokenHashUtilTest {

    @Test
    void sameInput_shouldProduceSameHash() {
        String token = "test-token";

        String hash1 = TokenHashUtil.sha256(token);
        String hash2 = TokenHashUtil.sha256(token);

        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    void differentInput_shouldProduceDifferentHash() {
        String hash1 = TokenHashUtil.sha256("token1");
        String hash2 = TokenHashUtil.sha256("token2");

        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    void hash_shouldHaveExpectedLength() {
        String hash = TokenHashUtil.sha256("anything");

        assertThat(hash).hasSize(64);
    }
}

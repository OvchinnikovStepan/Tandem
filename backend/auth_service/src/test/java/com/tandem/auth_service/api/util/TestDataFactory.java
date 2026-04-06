package com.tandem.auth_service.api.util;

import java.util.Locale;
import java.util.UUID;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static String randomEmail(String prefix) {
        return String.format(Locale.ROOT, "%s-%s@test.local", prefix, UUID.randomUUID());
    }

    public static String randomPhone() {
        long randomPart = (long) (Math.random() * 9_000_000_000L) + 1_000_000_000L;
        return "+7" + randomPart;
    }
}

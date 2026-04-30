package com.tandem.interest_service.api.util;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static String randomEmail(String prefix) {
        return String.format(Locale.ROOT, "%s-%s@test.local", prefix, UUID.randomUUID());
    }

    public static String randomPhone() {
        long randomPart = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
        return "+1" + randomPart;
    }

    public static String randomTagName() {
        return "tag-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String randomTagName(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}

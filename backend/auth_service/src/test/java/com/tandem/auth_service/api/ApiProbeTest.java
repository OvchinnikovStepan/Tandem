package com.tandem.auth_service.api;

import org.junit.jupiter.api.Test;
import io.qameta.allure.Epic;

@Epic("API Probe")
class ApiProbeTest {

    private static final AuthApiClient CLIENT = new AuthApiClient();

    @Test
    void probe_all_endpoints() {
        System.out.println("=== PROBING ALL ENDPOINTS ===\n");

        var regPhone = CLIENT.send("POST", "/register/phone",
                java.util.Map.of("phoneNumber", "+12345678901"), null);
        System.out.printf("POST /register/phone -> %d | %s%n%n", regPhone.statusCode(), regPhone.body());

        var regVerify = CLIENT.send("POST", "/register/verify",
                java.util.Map.of("verificationId", "test-id-123", "code", "123456"), null);
        System.out.printf("POST /register/verify -> %d | %s%n%n", regVerify.statusCode(), regVerify.body());

        var regEmail = CLIENT.send("POST", "/register/email",
                java.util.Map.of("verificationId", "test-id-123", "email", "probe@test.com", "password", "Qwerty123"), null);
        System.out.printf("POST /register/email -> %d | %s%n%n", regEmail.statusCode(), regEmail.body());

        var login = CLIENT.send("POST", "/login",
                java.util.Map.of("email", "probe@test.com", "password", "Qwerty123"), null);
        System.out.printf("POST /login -> %d | %s%n%n", login.statusCode(), login.body());

        var login2 = CLIENT.send("POST", "/login",
                java.util.Map.of("email", "user@example.com", "password", "password123"), null);
        System.out.printf("POST /login (example) -> %d | %s%n%n", login2.statusCode(), login2.body());

        var refresh = CLIENT.send("POST", "/refresh",
                java.util.Map.of("refreshToken", "dummy-token"), null);
        System.out.printf("POST /refresh -> %d | %s%n%n", refresh.statusCode(), refresh.body());

        var meNoToken = CLIENT.send("GET", "/me", null, null);
        System.out.printf("GET /me (no token) -> %d | %s%n%n", meNoToken.statusCode(), meNoToken.body());

        var meFake = CLIENT.send("GET", "/me", null, "fake-bearer-token");
        System.out.printf("GET /me (fake token) -> %d | %s%n%n", meFake.statusCode(), meFake.body());

        var logout = CLIENT.send("POST", "/logout", null, "fake-bearer-token");
        System.out.printf("POST /logout (fake token) -> %d | %s%n%n", logout.statusCode(), logout.body());

        var pwdStrength = CLIENT.send("POST", "/password/check-strength",
                java.util.Map.of("password", "Qwerty123"), null);
        System.out.printf("POST /password/check-strength -> %d | %s%n%n", pwdStrength.statusCode(), pwdStrength.body());

        var resetReq = CLIENT.send("POST", "/password/reset/request",
                java.util.Map.of("identifier", "probe@test.com", "method", "email"), null);
        System.out.printf("POST /password/reset/request -> %d | %s%n%n", resetReq.statusCode(), resetReq.body());

        var resetVerify = CLIENT.send("POST", "/password/reset/verify",
                java.util.Map.of("resetToken", "dummy-reset-token"), null);
        System.out.printf("POST /password/reset/verify -> %d | %s%n%n", resetVerify.statusCode(), resetVerify.body());

        var resetComplete = CLIENT.send("POST", "/password/reset/complete",
                java.util.Map.of("resetToken", "dummy-reset-token", "newPassword", "NewPass123"), null);
        System.out.printf("POST /password/reset/complete -> %d | %s%n%n", resetComplete.statusCode(), resetComplete.body());

        var sessions = CLIENT.send("GET", "/sessions", null, "fake-bearer-token");
        System.out.printf("GET /sessions (fake token) -> %d | %s%n%n", sessions.statusCode(), sessions.body());

        String verificationId = regPhone.body().path("verificationId").asText("");
        if (!verificationId.isBlank()) {
            System.out.println("=== TRYING VERIFICATION CODES ===\n");
            System.out.printf("Got verificationId: %s%n%n", verificationId);

            String[] codes = {"000000", "111111", "222222", "123456", "654321", "999999", "100000", "112233"};
            for (String code : codes) {
                var freshReg = CLIENT.send("POST", "/register/phone",
                        java.util.Map.of("phoneNumber", "+1234567" + code.substring(0, 4)), null);
                String freshVid = freshReg.body().path("verificationId").asText("");

                var verify = CLIENT.send("POST", "/register/verify",
                        java.util.Map.of("verificationId", freshVid, "code", code), null);
                System.out.printf("code=%s vid=%s -> %d | %s%n", code, freshVid, verify.statusCode(), verify.body());

                if (verify.statusCode() == 200) {
                    System.out.println("*** FOUND CORRECT CODE: " + code + " ***");

                    var emailReg = CLIENT.send("POST", "/register/email",
                            java.util.Map.of("verificationId", freshVid, "email", "fullflow@test.com", "password", "Qwerty123"), null);
                    System.out.printf("POST /register/email -> %d | %s%n%n", emailReg.statusCode(), emailReg.body());

                    var loginAfterReg = CLIENT.send("POST", "/login",
                            java.util.Map.of("email", "fullflow@test.com", "password", "Qwerty123"), null);
                    System.out.printf("POST /login (after reg) -> %d | %s%n%n", loginAfterReg.statusCode(), loginAfterReg.body());

                    String at = loginAfterReg.body().path("accessToken").asText("");
                    if (at.isBlank()) at = emailReg.body().path("accessToken").asText("");
                    if (!at.isBlank()) {
                        var meReal = CLIENT.send("GET", "/me", null, at);
                        System.out.printf("GET /me (real token) -> %d | %s%n%n", meReal.statusCode(), meReal.body());

                        String rt = loginAfterReg.body().path("refreshToken").asText("");
                        if (rt.isBlank()) rt = emailReg.body().path("refreshToken").asText("");

                        if (!rt.isBlank()) {
                            var refreshReal = CLIENT.send("POST", "/refresh",
                                    java.util.Map.of("refreshToken", rt), null);
                            System.out.printf("POST /refresh (real) -> %d | %s%n%n", refreshReal.statusCode(), refreshReal.body());
                        }

                        var logoutReal = CLIENT.send("POST", "/logout", null, at);
                        System.out.printf("POST /logout (real) -> %d | %s%n%n", logoutReal.statusCode(), logoutReal.body());
                    }
                    break;
                }
            }
        }

        System.out.println("=== PROBE COMPLETE ===");
    }
}

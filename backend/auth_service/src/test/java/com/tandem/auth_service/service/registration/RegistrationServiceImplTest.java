package com.tandem.auth_service.service.registration;

import com.tandem.auth_service.api.dto.PasswordStrength;
import com.tandem.auth_service.api.dto.response.RegisterEmailResponse;
import com.tandem.auth_service.api.error.exceptions.VerificationCodeInvalidException;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.session.SessionService;
import com.tandem.auth_service.service.token.JwtService;
import com.tandem.auth_service.utils.IpExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegistrationServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordStrengthService passwordStrengthService;

    @Mock
    private IpExtractor ipExtractor;

    @Mock
    private SessionService sessionService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser(UUID id) {
        User user = User.builder().build();
        user.setId(id);
        user.setPhoneNumber("+123");
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        return user;
    }

    // ===== startPhoneRegistration =====

    @Test
    void startPhoneRegistration_success() {
        when(userRepository.findByPhoneNumber("+123"))
                .thenReturn(Optional.empty());

        UUID result = registrationService.startPhoneRegistration("+123");

        assertNotNull(result);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void startPhoneRegistration_alreadyExists() {
        when(userRepository.findByPhoneNumber("+123"))
                .thenReturn(Optional.of(User.builder().build()));

        assertThrows(IllegalStateException.class, () ->
                registrationService.startPhoneRegistration("+123")
        );

        verify(userRepository, never()).save(any());
    }

    // ===== verifyPhone =====

    @Test
    void verifyPhone_success() {
        UUID id = UUID.randomUUID();
        User user = createUser(id);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        registrationService.verifyPhone(id, "123456");

        assertTrue(user.isPhoneVerified());
        verify(userRepository).update(user);
    }

    @Test
    void verifyPhone_invalidCode() {
        assertThrows(VerificationCodeInvalidException.class, () ->
                registrationService.verifyPhone(UUID.randomUUID(), "000000")
        );
    }

    @Test
    void verifyPhone_userNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(VerificationCodeInvalidException.class, () ->
                registrationService.verifyPhone(id, "123456")
        );
    }

    // ===== completeRegistration =====

    @Test
    void completeRegistration_success() {
        UUID id = UUID.randomUUID();
        User user = createUser(id);
        user.setPhoneVerified(true);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("pass"))
                .thenReturn("hashed");

        when(jwtService.generateAccessToken(any(), any()))
                .thenReturn("access");

        when(passwordStrengthService.evaluate("pass"))
                .thenReturn(PasswordStrength.STRONG);

        when(ipExtractor.extractIp(request))
                .thenReturn("127.0.0.1");

        when(request.getHeader("User-Agent"))
                .thenReturn("JUnit");

        RegisterEmailResponse response =
                registrationService.completeRegistration(request, id, "mail@test.com", "pass");

        assertNotNull(response);
        assertEquals(id, response.userId());
        assertEquals("access", response.accessToken());

        verify(userRepository).update(user);
        verify(sessionService).createSession(
                any(),
                eq(id),
                eq("access"),
                anyString(),
                eq("127.0.0.1"),
                eq("JUnit")
        );
    }

    @Test
    void completeRegistration_userNotFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                registrationService.completeRegistration(request, id, "mail", "pass")
        );
    }

    @Test
    void completeRegistration_phoneNotVerified() {
        UUID id = UUID.randomUUID();
        User user = createUser(id);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () ->
                registrationService.completeRegistration(request, id, "mail", "pass")
        );
    }

    @Test
    void completeRegistration_alreadyRegistered() {
        UUID id = UUID.randomUUID();
        User user = createUser(id);
        user.setPhoneVerified(true);
        user.setEmailVerified(true);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () ->
                registrationService.completeRegistration(request, id, "mail", "pass")
        );
    }
}

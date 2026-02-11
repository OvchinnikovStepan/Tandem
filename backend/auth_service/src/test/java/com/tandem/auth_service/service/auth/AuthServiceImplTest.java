package com.tandem.auth_service.service.auth;

import com.tandem.auth_service.api.dto.UserDto;
import com.tandem.auth_service.api.dto.response.LoginResponse;
import com.tandem.auth_service.model.User;
import com.tandem.auth_service.repository.UserRepository;
import com.tandem.auth_service.service.session.SessionService;
import com.tandem.auth_service.service.token.JwtService;
import com.tandem.auth_service.utils.IpExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private IpExtractor ipExtractor;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser() {
        User user = User.builder().build();
        user.setId(UUID.randomUUID());
        user.setEmail("test@mail.com");
        user.setPasswordHash("hashed");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    // ===== LOGIN TESTS =====

    @Test
    void login_success() {
        User user = createUser();

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "hashed"))
                .thenReturn(true);

        when(jwtService.generateAccessToken(any(), any()))
                .thenReturn("access-token");

        when(ipExtractor.extractIp(request))
                .thenReturn("127.0.0.1");

        when(request.getHeader("User-Agent"))
                .thenReturn("JUnit");

        LoginResponse response = authService.login(request, "test@mail.com", "password");

        assertNotNull(response);
        assertEquals("access-token", response.accessToken());
        assertNotNull(response.refreshToken());

        verify(userRepository).update(user);

        verify(sessionService).createSession(
                any(),
                eq(user.getId()),
                eq("access-token"),
                anyString(),
                eq("127.0.0.1"),
                eq("JUnit")
        );
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByEmail("bad@mail.com"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                authService.login(request, "bad@mail.com", "password")
        );

        verify(sessionService, never()).createSession(any(), any(), any(), any(), any(), any());
    }

    @Test
    void login_invalidPassword() {
        User user = createUser();

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches("wrong", "hashed"))
                .thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                authService.login(request, "test@mail.com", "wrong")
        );

        verify(sessionService, never()).createSession(any(), any(), any(), any(), any(), any());
    }

    @Test
    void login_nullPasswordHash() {
        User user = createUser();
        user.setPasswordHash(null);

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class, () ->
                authService.login(request, "test@mail.com", "password")
        );
    }

    @Test
    void login_updatesLastLoginAt() {
        User user = createUser();

        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.matches(any(), any()))
                .thenReturn(true);

        when(jwtService.generateAccessToken(any(), any()))
                .thenReturn("token");

        when(ipExtractor.extractIp(any()))
                .thenReturn("1.1.1.1");

        authService.login(request, "test@mail.com", "password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).update(captor.capture());

        assertNotNull(captor.getValue().getLastLoginAt());
    }

    // ===== GET CURRENT USER TESTS =====

    @Test
    void getCurrentUser_success() {
        User user = createUser();

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        UserDto dto = authService.getCurrentUser(user.getId());

        assertNotNull(dto);
        assertEquals(user.getId(), dto.id());
        assertEquals(user.getEmail(), dto.email());
    }

    @Test
    void getCurrentUser_notFound() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                authService.getCurrentUser(id)
        );
    }
}

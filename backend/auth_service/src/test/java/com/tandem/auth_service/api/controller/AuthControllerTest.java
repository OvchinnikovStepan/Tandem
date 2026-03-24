package com.tandem.auth_service.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.auth_service.api.dto.RefreshResultDto;
import com.tandem.auth_service.api.dto.SessionDto;
import com.tandem.auth_service.api.dto.UserDto;
import com.tandem.auth_service.api.dto.request.LoginRequest;
import com.tandem.auth_service.api.dto.request.RefreshTokenRequest;
import com.tandem.auth_service.api.dto.request.RegisterPhoneRequest;
import com.tandem.auth_service.api.dto.request.VerifyPhoneRequest;
import com.tandem.auth_service.api.dto.response.LoginResponse;
import com.tandem.auth_service.security.AuthPrincipal;
import com.tandem.auth_service.service.auth.AuthService;
import com.tandem.auth_service.service.registration.RegistrationService;
import com.tandem.auth_service.service.session.SessionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientAutoConfiguration;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("null")
@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class
    }
)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private SessionService sessionService;

    // --- REGISTER PHONE ---

    @Test
    void registerPhone_shouldReturnVerificationId() throws Exception {
        UUID verificationId = UUID.randomUUID();

        when(registrationService.startPhoneRegistration(any()))
                .thenReturn(verificationId);

        RegisterPhoneRequest request =
                new RegisterPhoneRequest("+1234567890");

        mockMvc.perform(post("/api/auth/register/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationId")
                        .value(verificationId.toString()));

        verify(registrationService, times(1))
                .startPhoneRegistration("+1234567890");
    }

    // --- VERIFY PHONE ---

    @Test
    void verifyPhone_shouldReturnSuccess() throws Exception {
        VerifyPhoneRequest request =
                new VerifyPhoneRequest(UUID.randomUUID(), "1234");

        mockMvc.perform(post("/api/auth/register/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified").value(true));

        verify(registrationService)
                .verifyPhone(any(UUID.class), eq("1234"));
    }

    // --- LOGIN ---

    @Test
    void login_shouldReturnTokens() throws Exception {
        LoginRequest request =
                new LoginRequest("test@example.com", "password");

        LoginResponse response =
                new LoginResponse("access", "refresh");

        when(authService.login(any(), any(), any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));

        verify(authService)
                .login(any(), eq("test@example.com"), eq("password"));
    }

    // --- REFRESH ---

    @Test
    void refresh_shouldReturnNewTokens() throws Exception {
        RefreshTokenRequest request =
                new RefreshTokenRequest("oldRefresh");

        when(sessionService.refresh("oldRefresh"))
                .thenReturn(new RefreshResultDto("newAccess", "newRefresh"));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("newAccess"))
                .andExpect(jsonPath("$.refreshToken").value("newRefresh"));

        verify(sessionService).refresh("oldRefresh");
    }

    // --- GET SESSIONS ---

    @Test
    void sessions_shouldReturnActiveSessions() throws Exception {
        UUID userId = UUID.randomUUID();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal())
                .thenReturn(new AuthPrincipal(userId, UUID.randomUUID()));

        List<SessionDto> sessions = List.of(
                new SessionDto(UUID.randomUUID(), userId, "device", "ip", null, null)
        );

        when(sessionService.getActiveSessions(userId))
                .thenReturn(sessions);

        mockMvc.perform(get("/api/auth/sessions")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessions").isArray());

        verify(sessionService).getActiveSessions(userId);
    }

    // --- LOGOUT ---

    @Test
    void logout_shouldCallService() throws Exception {
        UUID sessionId = UUID.randomUUID();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal())
                .thenReturn(new AuthPrincipal(UUID.randomUUID(), sessionId));

        mockMvc.perform(post("/api/auth/logout")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(sessionService).logout(sessionId);
    }

    // --- ME ---

    @Test
    void me_shouldReturnCurrentUser() throws Exception {
        UUID userId = UUID.randomUUID();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal())
                .thenReturn(new AuthPrincipal(userId, UUID.randomUUID()));

        when(authService.getCurrentUser(userId))
                .thenReturn(new UserDto(userId, "test@example.com", null, false, false, null, null));

        mockMvc.perform(get("/api/auth/me")
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email")
                        .value("test@example.com"));

        verify(authService).getCurrentUser(userId);
    }

    // --- TERMINATE SESSION ---

    @Test
    void terminateSession_shouldCallService() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal())
                .thenReturn(new AuthPrincipal(userId, UUID.randomUUID()));

        mockMvc.perform(delete("/api/auth/sessions/" + sessionId)
                        .principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(sessionService)
                .terminateSession(userId, sessionId);
    }
}

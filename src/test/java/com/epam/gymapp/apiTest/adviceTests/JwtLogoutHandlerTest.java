package com.epam.gymapp.apiTest.adviceTests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.JwtLogoutHandler;
import com.epam.gymapp.entities.RevokedToken;
import com.epam.gymapp.repositories.RevokedTokenRepo;
import com.epam.gymapp.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class JwtLogoutHandlerTest {

  @Mock private JwtService jwtService;
  @Mock private RevokedTokenRepo repo;
  @Mock private HttpServletRequest req;
  @Mock private HttpServletResponse res;
  @Mock private Authentication auth;
  @Mock private Jwt jwt;

  private JwtLogoutHandler handler;

  @BeforeEach
  void setUp() {
    handler = new JwtLogoutHandler(jwtService, repo);
  }

  @Test
  void logout_noAuthorizationHeader_unauthorizedResponseAndNoTokenSaved() {
    when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    handler.logout(req, res, auth);

    verify(res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(repo);
  }

  @Test
  void logout_nonBearerAuthorizationHeader_unauthorizedResponseAndNoTokenSaved() {
    when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic xyz");

    handler.logout(req, res, auth);

    verify(res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verifyNoInteractions(jwtService);
    verifyNoInteractions(repo);
  }

  @Test
  void logout_validBearerToken_tokenParsedAndRevokedTokenSaved() {
    String token = "token-123";
    Instant expiresAt = Instant.now().plusSeconds(3600);

    when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(jwtService.parse(token)).thenReturn(jwt);
    when(jwt.getExpiresAt()).thenReturn(expiresAt);

    handler.logout(req, res, auth);

    ArgumentCaptor<RevokedToken> captor = ArgumentCaptor.forClass(RevokedToken.class);
    verify(repo).save(captor.capture());
    RevokedToken saved = captor.getValue();

    assertEquals(token, saved.getToken());
    assertEquals(expiresAt, saved.getExpiresAt());

    verify(res, never()).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(jwtService).parse(token);
  }
}

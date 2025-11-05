package com.epam.gymapp.apiTest.controllersTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.controllers.AuthController;
import com.epam.gymapp.api.dto.ChangePasswordDto;
import com.epam.gymapp.api.dto.TokenDto;
import com.epam.gymapp.entities.RevokedToken;
import com.epam.gymapp.repositories.RevokedTokenRepo;
import com.epam.gymapp.services.AuthenticationService;
import com.epam.gymapp.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private AuthenticationService authService;
  @Mock private JwtService jwtService;
  @Mock private RevokedTokenRepo repo;
  @Mock private HttpServletRequest request;
  @InjectMocks private AuthController controller;

  @Test
  void login_validCredentials_tokenDtoWithOkStatus() {
    String username = "john";
    String password = "secret";
    String token = "jwt.token.here";

    when(jwtService.createToken(username)).thenReturn(token);

    ResponseEntity<TokenDto> response = controller.login(username, password);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(token, response.getBody().token());
    verify(authService).validate(username, password);
    verify(jwtService).createToken(username);
  }

  @Test
  void logout_validToken_tokenRevokedAndNoContentStatus() {
    String token = "Bearer jwt.token.here";
    String jwtToken = "jwt.token.here";
    Instant expiresAt = Instant.now().plusSeconds(3600);

    Jwt jwt = mock(Jwt.class);
    when(jwt.getExpiresAt()).thenReturn(expiresAt);

    when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(token);
    when(jwtService.parse(jwtToken)).thenReturn(jwt);

    ResponseEntity<Void> response = controller.logout(request);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    ArgumentCaptor<RevokedToken> captor = ArgumentCaptor.forClass(RevokedToken.class);
    verify(repo).save(captor.capture());

    RevokedToken savedToken = captor.getValue();
    assertEquals(jwtToken, savedToken.getToken());
    assertEquals(expiresAt, savedToken.getExpiresAt());
  }

  @Test
  void changePassword_validPasswordChange_okStatus() {
    String username = "alice";
    ChangePasswordDto dto = new ChangePasswordDto("oldPass", "newPass");

    ResponseEntity<Void> response = controller.changePassword(username, dto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(authService).validate(username, dto.oldPassword());
    verify(authService).changePassword(username, dto.newPassword());
  }
}

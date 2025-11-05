package com.epam.gymapp.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.UserRepository;
import com.epam.gymapp.services.AuthenticationService;
import com.epam.gymapp.services.LoginAttemptService;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock AuthenticationManager authManager;

  @Mock PasswordEncoder encoder;

  @Mock UserRepository userRepo;

  @Mock LoginAttemptService attemptService;

  private AuthenticationService service;

  @BeforeEach
  void setUp() {
    service = new AuthenticationService(authManager, encoder, userRepo, attemptService);
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
    reset(authManager, encoder, userRepo, attemptService);
  }

  @Test
  void validate_validCredentials_securityContextSetAndUserReturned() {
    String username = "john";
    String rawPassword = "secret";

    Authentication auth = mock(Authentication.class);
    when(authManager.authenticate(
            argThat(
                token ->
                    token instanceof UsernamePasswordAuthenticationToken
                        && username.equals(token.getName())
                        && rawPassword.equals(token.getCredentials()))))
        .thenReturn(auth);

    User expectedUser = new User();
    expectedUser.setUsername(username);
    when(userRepo.findByUsername(username)).thenReturn(Optional.of(expectedUser));

    User actual = service.validate(username, rawPassword);

    assertSame(expectedUser, actual);
    verify(attemptService).assertNotLocked(username);
    verify(attemptService).reset(username);
    assertSame(auth, SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void validate_invalidCredentials_failureRecordedAndApiExceptionThrown() {
    String username = "joe";
    String rawPassword = "wrong";

    when(authManager.authenticate(
            argThat(
                token ->
                    token instanceof UsernamePasswordAuthenticationToken
                        && username.equals(token.getName())
                        && rawPassword.equals(token.getCredentials()))))
        .thenThrow(new BadCredentialsException("bad credentials"));

    assertThrows(ApiException.class, () -> service.validate(username, rawPassword));

    verify(attemptService).assertNotLocked(username);
    verify(attemptService).recordFailure(username);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void changePassword_validUsernameAndPassword_passwordEncodedAndUpdated() {
    String username = "alice";
    String newPassword = "new-pass";
    String encoded = "encoded-pass";

    User user = new User();
    user.setUsername(username);
    user.setPassword("old");

    when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
    when(encoder.encode(newPassword)).thenReturn(encoded);

    service.changePassword(username, newPassword);

    assertEquals(encoded, user.getPassword());
    verify(userRepo).findByUsername(username);
    verify(encoder).encode(newPassword);
  }

  @Test
  void changePassword_nonExistentUser_apiExceptionThrown() {
    String username = "missing";
    String newPassword = "whatever";

    when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(ApiException.class, () -> service.changePassword(username, newPassword));

    verify(userRepo).findByUsername(username);
    verifyNoInteractions(encoder);
  }

  @Test
  void validate_authenticatedButUserNotFound_exceptionThrown() {
    String username = "authenticated";
    String rawPassword = "pass";

    Authentication auth = mock(Authentication.class);
    when(authManager.authenticate(any())).thenReturn(auth);
    when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

    assertThrows(Exception.class, () -> service.validate(username, rawPassword));

    verify(attemptService).assertNotLocked(username);
  }
}

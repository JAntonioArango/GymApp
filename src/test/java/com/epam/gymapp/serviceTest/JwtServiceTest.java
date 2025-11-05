package com.epam.gymapp.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.config.JwtConfig;
import com.epam.gymapp.services.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

  @Mock private JwtEncoder encoder;
  @Mock private JwtDecoder decoder;
  @Mock private JwtConfig jwtConfig;

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService(encoder, decoder, jwtConfig);
  }

  @Test
  void parse_validToken_decodedJwtReturned() {
    String token = "some-jwt";
    Jwt jwt = mock(Jwt.class);
    when(decoder.decode(token)).thenReturn(jwt);

    Jwt result = jwtService.parse(token);

    assertSame(jwt, result);
    verify(decoder).decode(token);
  }
}

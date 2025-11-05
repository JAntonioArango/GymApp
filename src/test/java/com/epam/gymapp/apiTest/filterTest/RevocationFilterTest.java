package com.epam.gymapp.apiTest.filterTest;

import static org.mockito.Mockito.*;

import com.epam.gymapp.api.filter.RevocationFilter;
import com.epam.gymapp.repositories.RevokedTokenRepo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class RevocationFilterTest {

  @Mock private RevokedTokenRepo repo;
  @Mock private HttpServletRequest req;
  @Mock private HttpServletResponse res;
  @Mock private FilterChain chain;

  private RevocationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new RevocationFilter(repo);
  }

  @Test
  void doFilter_noAuthorizationHeader_filterChainCalled() throws ServletException, IOException {
    when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

    filter.doFilter(req, res, chain);

    verify(chain).doFilter(req, res);
    verify(res, never()).sendError(anyInt(), anyString());
  }

  @Test
  void doFilter_nonBearerAuthorizationHeader_filterChainCalled()
      throws ServletException, IOException {
    when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Basic abcdef");

    filter.doFilter(req, res, chain);

    verify(chain).doFilter(req, res);
    verify(res, never()).sendError(anyInt(), anyString());
  }

  @Test
  void doFilter_validBearerToken_filterChainCalled() throws ServletException, IOException {
    String token = "valid-token";
    when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(repo.existsById(token)).thenReturn(false);

    filter.doFilter(req, res, chain);

    verify(chain).doFilter(req, res);
    verify(res, never()).sendError(anyInt(), anyString());
  }

  @Test
  void doFilter_revokedBearerToken_unauthorizedErrorSentAndChainNotCalled()
      throws ServletException, IOException {
    String token = "revoked-token";
    when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + token);
    when(repo.existsById(token)).thenReturn(true);

    filter.doFilter(req, res, chain);

    verify(res).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Revoked Token");
    verify(chain, never()).doFilter(req, res);
  }
}

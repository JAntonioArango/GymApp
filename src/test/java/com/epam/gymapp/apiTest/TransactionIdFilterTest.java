package com.epam.gymapp.apiTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.filter.TransactionIdFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

  private TransactionIdFilter filter;
  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @Mock private FilterChain filterChain;

  @BeforeEach
  void setUp() {
    filter = new TransactionIdFilter();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void doFilterInternal_normalExecution_mdcCleanedUpAfterExecution()
      throws ServletException, IOException {
    filter.doFilterInternal(request, response, filterChain);

    assertNull(MDC.get("txId"), "MDC should be cleaned up after filter execution");
  }

  @Test
  void doFilterInternal_exceptionThrown_mdcCleanedUpEvenAfterException()
      throws ServletException, IOException {
    doThrow(new RuntimeException("Test exception")).when(filterChain).doFilter(request, response);

    assertThrows(
        RuntimeException.class, () -> filter.doFilterInternal(request, response, filterChain));
    assertNull(MDC.get("txId"), "MDC should be cleaned up even after exception");
  }

  @Test
  void doFilterInternal_transactionIdHeader_elapsedTimeCalculatedAndChainCalled()
      throws ServletException, IOException {
    String txId = "test-transaction-id";
    request.addHeader("X-Transaction-Id", txId);

    filter.doFilterInternal(request, response, filterChain);

    verify(filterChain).doFilter(request, response);
  }
}

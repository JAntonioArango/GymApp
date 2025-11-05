package com.epam.gymapp.apiTest.adviceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.advice.RestExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

class RestExceptionHandlerTest {
  private RestExceptionHandler handler;
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    handler = new RestExceptionHandler();
    request = mock(HttpServletRequest.class);
    when(request.getRequestURI()).thenReturn("/test/path");
  }

  @Test
  void notFound_entityNotFoundException_notFoundProblemDetailReturned() {
    EntityNotFoundException ex = new EntityNotFoundException("TestEntity");
    ResponseEntity<ProblemDetail> response = handler.notFound(ex, request);
    ProblemDetail pd = response.getBody();

    assertNotNull(pd);
    assertEquals(404, pd.getStatus());
    assertEquals("Resource not found", pd.getTitle());
    assertEquals("TestEntity", pd.getDetail());
  }

  @Test
  void apiError_apiException_problemDetailWithCorrectStatusReturned() {
    ApiException ex = ApiException.badRequest("Bad things happened");
    ResponseEntity<ProblemDetail> response = handler.apiError(ex, request);
    ProblemDetail pd = response.getBody();

    assertNotNull(pd);
    assertEquals(ex.getStatus().value(), pd.getStatus());
    assertEquals(ex.getStatus().getReasonPhrase(), pd.getTitle());
    assertEquals("Bad things happened", pd.getDetail());
  }
}

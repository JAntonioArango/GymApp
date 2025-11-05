package com.epam.gymapp.apiTest.adviceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.advice.GlobalExceptionHandler;
import com.epam.gymapp.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler exceptionHandler;

  @Mock private HttpServletRequest request;

  @Mock private MethodArgumentNotValidException methodArgumentNotValidException;

  @Mock private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    exceptionHandler = new GlobalExceptionHandler();
    when(request.getRequestURI()).thenReturn("/api/test");
  }

  @Test
  void handleApiException_apiException_correctResponseEntityReturned() {
    HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
    String expectedMessage = "Resource not found";
    ApiException apiException = new ApiException(expectedStatus, expectedMessage);

    ResponseEntity<ErrorResponse> responseEntity =
        exceptionHandler.handleApiException(apiException, request);

    assertNotNull(responseEntity);
    assertEquals(expectedStatus, responseEntity.getStatusCode());
    ErrorResponse error = responseEntity.getBody();
    assertNotNull(error);
  }

  @Test
  void handleValidation_validationException_badRequestResponseReturned() {
    List<FieldError> fieldErrors =
        List.of(
            new FieldError("object", "field1", "must not be blank"),
            new FieldError("object", "field2", "must be positive"));

    when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

    ResponseEntity<ErrorResponse> responseEntity =
        exceptionHandler.handleValidation(methodArgumentNotValidException, request);

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    ErrorResponse error = responseEntity.getBody();
    assertNotNull(error);
  }

  @Test
  void handleAll_unexpectedException_internalServerErrorReturned() {
    String expectedMessage = "Unexpected error";
    Exception exception = new RuntimeException(expectedMessage);

    ResponseEntity<ErrorResponse> responseEntity = exceptionHandler.handleAll(exception, request);

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    ErrorResponse error = responseEntity.getBody();
    assertNotNull(error);
  }
}

package com.epam.gymapp.api.advice;

import com.epam.gymapp.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final String GENERIC_ERROR_MESSAGE = "An unexpected error occurred";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest req) {
    String details =
        ex.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining("; "));
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            details,
            req.getRequestURI());
    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest req) {
    log.error("Unhandled exception on {}", req.getRequestURI(), ex);
    ErrorResponse error =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            GENERIC_ERROR_MESSAGE,
            req.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}

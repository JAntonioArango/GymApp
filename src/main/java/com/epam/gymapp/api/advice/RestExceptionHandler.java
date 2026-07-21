package com.epam.gymapp.api.advice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ProblemDetail> notFound(
      EntityNotFoundException ex, HttpServletRequest req) {

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    pd.setTitle("Resource not found");
    pd.setDetail(ex.getMessage());
    pd.setProperty("path", req.getRequestURI());

    return ResponseEntity.status(pd.getStatus()).body(pd);
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ProblemDetail> apiError(ApiException ex, HttpServletRequest req) {

    ProblemDetail pd = ProblemDetail.forStatus(ex.getStatus());
    pd.setTitle(ex.getStatus().getReasonPhrase());
    pd.setDetail(ex.getMessage());
    pd.setProperty("path", req.getRequestURI());
    return ResponseEntity.status(ex.getStatus()).body(pd);
  }

  @ExceptionHandler(LockedException.class)
  public ResponseEntity<ProblemDetail> locked(LockedException ex, HttpServletRequest req) {

    ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.LOCKED);
    pd.setTitle("Account temporarily locked");
    pd.setDetail(ex.getMessage());
    pd.setProperty("path", req.getRequestURI());
    return ResponseEntity.status(pd.getStatus()).body(pd);
  }
}

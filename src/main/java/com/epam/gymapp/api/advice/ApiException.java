package com.epam.gymapp.api.advice;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

  private final HttpStatus status;

  public ApiException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public static ApiException duplicate(String message, @NotBlank String username) {
    return new ApiException(HttpStatus.CONFLICT, message + ": " + username + " already exists");
  }

  public HttpStatus getStatus() {
    return status;
  }

  public static ApiException notFound(String entity, Object idOrName) {
    return new ApiException(HttpStatus.NOT_FOUND, entity + " not found: " + idOrName);
  }

  public static ApiException badCredentials() {
    return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
  }

  public static ApiException badRequest(String message) {
    return new ApiException(HttpStatus.BAD_REQUEST, message);
  }
}

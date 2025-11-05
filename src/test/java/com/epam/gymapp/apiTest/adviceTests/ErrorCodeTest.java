package com.epam.gymapp.apiTest.adviceTests;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.api.advice.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorCodeTest {
  @Test
  void getStatus_allErrorCodes_correctHttpStatusReturned() {
    assertEquals(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND.getStatus());
    assertEquals("Resource Not Found", ErrorCode.RESOURCE_NOT_FOUND.getTitle());

    assertEquals(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_FAILED.getStatus());
    assertEquals("Validation Failed", ErrorCode.VALIDATION_FAILED.getTitle());

    assertEquals(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED.getStatus());
    assertEquals("Unauthorized", ErrorCode.UNAUTHORIZED.getTitle());

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR.getStatus());
    assertEquals("Internal Server Error", ErrorCode.INTERNAL_ERROR.getTitle());
  }
}

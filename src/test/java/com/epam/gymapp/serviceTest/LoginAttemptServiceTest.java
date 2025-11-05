package com.epam.gymapp.serviceTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.services.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.LockedException;

class LoginAttemptServiceTest {

  private LoginAttemptService service;
  private static final String USER = "alice";

  @BeforeEach
  void setUp() {
    service = new LoginAttemptService();
  }

  @Test
  void assertNotLocked_noFailureAttempts_noExceptionThrown() {
    assertDoesNotThrow(() -> service.assertNotLocked(USER));
  }

  @Test
  void assertNotLocked_thresholdExceeded_apiExceptionThrown() {
    for (int i = 0; i < 3; i++) {
      try {
        service.recordFailure(USER);
      } catch (LockedException ignored) {
      }
    }
    assertThrows(ApiException.class, () -> service.assertNotLocked(USER));
  }

  @Test
  void recordFailure_belowThreshold_noExceptionThrown() {
    assertDoesNotThrow(() -> service.recordFailure(USER));
    assertDoesNotThrow(() -> service.recordFailure(USER));
  }

  @Test
  void recordFailure_thresholdReached_lockedExceptionThrown() {
    service.recordFailure(USER);
    service.recordFailure(USER);
    assertThrows(LockedException.class, () -> service.recordFailure(USER));
  }

  @Test
  void reset_lockedUser_counterClearedAndUserCanLogin() {
    for (int i = 0; i < 3; i++) {
      try {
        service.recordFailure(USER);
      } catch (LockedException ignored) {
      }
    }
    service.reset(USER);
    assertDoesNotThrow(() -> service.assertNotLocked(USER));
  }
}

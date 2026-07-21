package com.epam.gymapp.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.entities.LoginAttempt;
import com.epam.gymapp.repositories.LoginAttemptRepo;
import com.epam.gymapp.services.LoginAttemptService;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.LockedException;

@ExtendWith(MockitoExtension.class)
class LoginAttemptServiceTest {

  @Mock private LoginAttemptRepo attemptRepo;

  @InjectMocks private LoginAttemptService service;

  private static final String USER = "alice";

  private AtomicReference<LoginAttempt> stored;

  @BeforeEach
  void setUp() {
    stored = new AtomicReference<>();
    lenient().when(attemptRepo.findById(USER)).thenAnswer(inv -> Optional.ofNullable(stored.get()));
    lenient()
        .when(attemptRepo.save(any(LoginAttempt.class)))
        .thenAnswer(
            inv -> {
              LoginAttempt saved = inv.getArgument(0);
              stored.set(saved);
              return saved;
            });
    lenient().when(attemptRepo.existsById(USER)).thenAnswer(inv -> stored.get() != null);
    lenient()
        .doAnswer(
            inv -> {
              stored.set(null);
              return null;
            })
        .when(attemptRepo)
        .deleteById(USER);
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

package com.epam.gymapp.services;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.entities.LoginAttempt;
import com.epam.gymapp.repositories.LoginAttemptRepo;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

  private static final int MAX_ATTEMPTS = 3;
  private static final int BLOCKED_MINUTES = 5;
  private static final String LOCKED = "Account locked for 5 minutes";

  private final LoginAttemptRepo attemptRepo;

  public void assertNotLocked(String username) {
    attemptRepo
        .findById(username)
        .map(LoginAttempt::getLockedUntil)
        .filter(lockedUntil -> lockedUntil.isAfter(Instant.now()))
        .ifPresent(
            lockedUntil -> {
              throw ApiException.badRequest(LOCKED);
            });
  }

  @Transactional
  public void recordFailure(String username) {
    LoginAttempt attempt =
        attemptRepo.findById(username).orElseGet(() -> new LoginAttempt(username, 0, null, 0));

    int newCount = attempt.getFailureCount() + 1;
    attempt.setFailureCount(newCount);

    if (newCount >= MAX_ATTEMPTS) {
      attempt.setLockedUntil(Instant.now().plus(BLOCKED_MINUTES, ChronoUnit.MINUTES));
      attemptRepo.save(attempt);
      throw new LockedException(LOCKED);
    }

    attemptRepo.save(attempt);
  }

  @Transactional
  public void reset(String username) {
    if (attemptRepo.existsById(username)) {
      attemptRepo.deleteById(username);
    }
  }
}

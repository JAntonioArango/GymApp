package com.epam.gymapp.utils;

import com.epam.gymapp.api.advice.ApiException;
import java.util.Optional;
import java.util.function.Function;

public final class ProfileUpdateSupport {

  private ProfileUpdateSupport() {}

  public static String sanitizeUsername(String username) {
    return username.replaceAll("\\s", ".");
  }

  public static <T> void assertUsernameAvailable(
      Function<String, Optional<T>> findByUsername,
      Function<T, Long> idOf,
      Long currentId,
      String newUsername) {

    findByUsername
        .apply(newUsername)
        .ifPresent(
            existing -> {
              if (!idOf.apply(existing).equals(currentId)) {
                throw ApiException.duplicate("Username", newUsername);
              }
            });
  }
}

package com.epam.gymapp.apiTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.epam.gymapp.repositories.UserRepository;
import com.epam.gymapp.utils.CredentialGenerator;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CredentialGeneratorTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private CredentialGenerator generator;

  @Test
  void randomPassword_noParameters_tenCharacterAlphanumericPassword() {
    String password = generator.randomPassword();
    assertNotNull(password);
    assertEquals(10, password.length());
    assertTrue(password.matches("[A-Za-z0-9]{10}"));
  }

  @Test
  void randomPassword_multipleGenerations_uniquePasswordsGenerated() {
    Set<String> passwords = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      passwords.add(generator.randomPassword());
    }
    assertEquals(100, passwords.size(), "Passwords should be unique");
  }

  @Test
  void buildUniqueUsername_noCollision_validFormattedUsername() {
    when(userRepository.existsByUsername(anyString())).thenReturn(false);
    String username = generator.buildUniqueUsername("John", "Doe");
    assertTrue(username.matches("john\\.doe-[0-9a-f]{8}"));
    verify(userRepository).existsByUsername(username);
  }

  @Test
  void buildUniqueUsername_singleCollision_uniqueUsernameGenerated() {
    when(userRepository.existsByUsername(anyString())).thenReturn(true).thenReturn(false);

    String username = generator.buildUniqueUsername("John", "Doe");

    assertTrue(username.matches("john\\.doe-[0-9a-f]{8}"));
    verify(userRepository, atLeast(2)).existsByUsername(anyString());
  }

  @Test
  void buildUniqueUsername_multipleCollisions_uniqueUsernameEventuallyGenerated() {
    when(userRepository.existsByUsername(anyString()))
        .thenReturn(true, true, true, true, true)
        .thenReturn(false);

    String username = generator.buildUniqueUsername("John", "Doe");

    assertTrue(username.matches("john\\.doe-[0-9a-f]{8}"));
    verify(userRepository, atLeast(6)).existsByUsername(anyString());
  }

  @Test
  void buildUniqueUsername_differentCases_lowercaseUsernamesWithDifferentSuffixes() {
    when(userRepository.existsByUsername(anyString())).thenReturn(false);

    String username1 = generator.buildUniqueUsername("JOHN", "DOE");
    String username2 = generator.buildUniqueUsername("john", "doe");

    assertTrue(username1.startsWith("john.doe-"));
    assertTrue(username2.startsWith("john.doe-"));
    assertNotEquals(username1, username2);
  }

  @Test
  void buildUniqueUsername_specialCharacters_specialCharactersPreservedInUsername() {
    when(userRepository.existsByUsername(anyString())).thenReturn(false);

    String username = generator.buildUniqueUsername("O'Neil-Smith", "Van der Berg");

    assertTrue(username.startsWith("o'neil-smith.van der berg-"));
    assertTrue(username.matches("o'neil-smith\\.van der berg-[0-9a-f]{8}"));
  }
}

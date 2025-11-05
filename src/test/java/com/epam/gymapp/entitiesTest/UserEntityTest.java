package com.epam.gymapp.entitiesTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserEntityTest {
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
  }

  @Test
  void constructor_noArgs_defaultValuesSet() {
    assertNotNull(user);
    assertNull(user.getId());
    assertNull(user.getFirstName());
    assertNull(user.getLastName());
    assertNull(user.getUsername());
    assertNull(user.getPassword());
    assertTrue(user.isActive());
    assertNull(user.getTrainer());
    assertNull(user.getTrainee());
  }

  @Test
  void constructor_allArgs_fieldsSetCorrectly() {
    Trainer trainer = new Trainer();
    Trainee trainee = new Trainee();

    User user = new User(1L, "John", "Doe", "johndoe", "password123", true, trainer, trainee);

    assertEquals(1L, user.getId());
    assertEquals("John", user.getFirstName());
    assertEquals("Doe", user.getLastName());
    assertEquals("johndoe", user.getUsername());
    assertEquals("password123", user.getPassword());
    assertTrue(user.isActive());
    assertEquals(trainer, user.getTrainer());
    assertEquals(trainee, user.getTrainee());
  }

  @Test
  void setId_validId_idStored() {
    user.setId(1L);
    assertEquals(1L, user.getId());
  }

  @Test
  void setFirstName_validName_firstNameStored() {
    user.setFirstName("John");
    assertEquals("John", user.getFirstName());
  }

  @Test
  void setLastName_validName_lastNameStored() {
    user.setLastName("Doe");
    assertEquals("Doe", user.getLastName());
  }

  @Test
  void setUsername_validUsername_usernameStored() {
    user.setUsername("johndoe");
    assertEquals("johndoe", user.getUsername());
  }

  @Test
  void setPassword_validPassword_passwordStored() {
    user.setPassword("password123");
    assertEquals("password123", user.getPassword());
  }

  @Test
  void setActive_booleanValues_activeStatusUpdated() {
    user.setActive(false);
    assertFalse(user.isActive());

    user.setActive(true);
    assertTrue(user.isActive());
  }

  @Test
  void isActive_newUser_defaultTrueValue() {
    User newUser = new User();
    assertTrue(newUser.isActive());
  }

  @Test
  void setTrainer_validTrainer_trainerStored() {
    Trainer trainer = new Trainer();
    user.setTrainer(trainer);
    assertEquals(trainer, user.getTrainer());
  }

  @Test
  void setTrainee_validTrainee_traineeStored() {
    Trainee trainee = new Trainee();
    user.setTrainee(trainee);
    assertEquals(trainee, user.getTrainee());
  }

  @Test
  void equals_sameUsername_objectsNotEqual() {
    User user1 = new User();
    user1.setUsername("uniqueusername");

    User user2 = new User();
    user2.setUsername("uniqueusername");

    assertNotEquals(user1, user2);
  }

  @Test
  void getTrainer_newUser_nullTrainerAndTrainee() {
    User user = new User();
    assertNull(user.getTrainer());
    assertNull(user.getTrainee());
  }
}

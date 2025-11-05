package com.epam.gymapp.entitiesTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.Training;
import com.epam.gymapp.entities.User;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TraineeEntityTest {
  private Trainee trainee;
  private User user;
  private LocalDate testDate;

  @BeforeEach
  void setUp() {
    trainee = new Trainee();
    user = new User();
    testDate = LocalDate.of(1990, 1, 1);
  }

  @Test
  void constructor_noArgs_defaultValuesAndEmptyCollections() {
    assertNotNull(trainee);
    assertNull(trainee.getId());
    assertNull(trainee.getDateOfBirth());
    assertNull(trainee.getAddress());
    assertNull(trainee.getUser());
    assertNotNull(trainee.getTrainers());
    assertNotNull(trainee.getTrainings());
    assertTrue(trainee.getTrainers().isEmpty());
    assertTrue(trainee.getTrainings().isEmpty());
  }

  @Test
  void constructor_allArgs_fieldsSetCorrectly() {
    Set<Trainer> trainers = new HashSet<>();
    Set<Training> trainings = new HashSet<>();

    Trainee trainee = new Trainee(1L, testDate, "123 Test Street", user, trainers, trainings);

    assertEquals(1L, trainee.getId());
    assertEquals(testDate, trainee.getDateOfBirth());
    assertEquals("123 Test Street", trainee.getAddress());
    assertEquals(user, trainee.getUser());
    assertEquals(trainers, trainee.getTrainers());
    assertEquals(trainings, trainee.getTrainings());
  }

  @Test
  void setId_validId_idStored() {
    trainee.setId(1L);
    assertEquals(1L, trainee.getId());
  }

  @Test
  void setDateOfBirth_validDate_dateStored() {
    trainee.setDateOfBirth(testDate);
    assertEquals(testDate, trainee.getDateOfBirth());
  }

  @Test
  void setAddress_validAddress_addressStored() {
    String address = "456 Test Avenue";
    trainee.setAddress(address);
    assertEquals(address, trainee.getAddress());
  }

  @Test
  void setUser_validUser_userStored() {
    trainee.setUser(user);
    assertEquals(user, trainee.getUser());
  }

  @Test
  void trainers_addAndRemove_collectionUpdated() {
    Trainer trainer = new Trainer();

    trainee.getTrainers().add(trainer);
    assertEquals(1, trainee.getTrainers().size());
    assertTrue(trainee.getTrainers().contains(trainer));

    trainee.getTrainers().remove(trainer);
    assertEquals(0, trainee.getTrainers().size());
    assertFalse(trainee.getTrainers().contains(trainer));
  }

  @Test
  void trainings_addAndRemove_collectionUpdated() {
    Training training = new Training();

    trainee.getTrainings().add(training);
    assertEquals(1, trainee.getTrainings().size());
    assertTrue(trainee.getTrainings().contains(training));

    trainee.getTrainings().remove(training);
    assertEquals(0, trainee.getTrainings().size());
    assertFalse(trainee.getTrainings().contains(training));
  }

  @Test
  void getTrainers_newInstance_emptySetInitialized() {
    Trainee newTrainee = new Trainee();
    assertNotNull(newTrainee.getTrainers());
    assertTrue(newTrainee.getTrainers().isEmpty());
  }

  @Test
  void getTrainings_newInstance_emptySetInitialized() {
    Trainee newTrainee = new Trainee();
    assertNotNull(newTrainee.getTrainings());
    assertTrue(newTrainee.getTrainings().isEmpty());
  }

  @Test
  void collections_multipleItems_correctSizesMaintained() {
    Trainer trainer1 = new Trainer();
    Trainer trainer2 = new Trainer();
    Training training1 = new Training();
    Training training2 = new Training();

    trainee.getTrainers().add(trainer1);
    trainee.getTrainers().add(trainer2);
    trainee.getTrainings().add(training1);
    trainee.getTrainings().add(training2);

    assertEquals(2, trainee.getTrainers().size());
    assertEquals(2, trainee.getTrainings().size());
  }

  @Test
  void training_cascadeOperation_bidirectionalRelationshipMaintained() {
    Training training = new Training();
    training.setTrainee(trainee);
    trainee.getTrainings().add(training);

    assertEquals(trainee, training.getTrainee());
    assertTrue(trainee.getTrainings().contains(training));
  }
}

package com.epam.gymapp.entitiesTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.Training;
import com.epam.gymapp.entities.TrainingType;
import com.epam.gymapp.entities.User;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrainerEntityTest {

  private Trainer trainer;
  private User user;
  private TrainingType specialization;

  @BeforeEach
  void setUp() {
    trainer = new Trainer();
    user = new User();
    specialization = new TrainingType();
  }

  @Test
  void constructor_noArgs_defaultValuesAndEmptyCollections() {
    assertNotNull(trainer);
    assertNotNull(trainer.getTrainees());
    assertNotNull(trainer.getTrainings());
    assertTrue(trainer.getTrainees().isEmpty());
    assertTrue(trainer.getTrainings().isEmpty());
  }

  @Test
  void constructor_allArgs_fieldsSetCorrectly() {
    Set<Trainee> trainees = new HashSet<>();
    Set<Training> trainings = new HashSet<>();

    Trainer trainer = new Trainer(1L, specialization, user, trainees, trainings);

    assertEquals(1L, trainer.getId());
    assertEquals(specialization, trainer.getSpecialization());
    assertEquals(user, trainer.getUser());
    assertEquals(trainees, trainer.getTrainees());
    assertEquals(trainings, trainer.getTrainings());
  }

  @Test
  void setId_validId_idStored() {
    trainer.setId(1L);
    assertEquals(1L, trainer.getId());
  }

  @Test
  void setSpecialization_validSpecialization_specializationStored() {
    trainer.setSpecialization(specialization);
    assertEquals(specialization, trainer.getSpecialization());
  }

  @Test
  void setUser_validUser_userStored() {
    trainer.setUser(user);
    assertEquals(user, trainer.getUser());
  }

  @Test
  void trainees_addAndRemove_collectionUpdated() {
    Trainee trainee = new Trainee();

    trainer.getTrainees().add(trainee);
    assertEquals(1, trainer.getTrainees().size());
    assertTrue(trainer.getTrainees().contains(trainee));

    trainer.getTrainees().remove(trainee);
    assertEquals(0, trainer.getTrainees().size());
    assertFalse(trainer.getTrainees().contains(trainee));
  }

  @Test
  void trainings_addAndRemove_collectionUpdated() {
    Training training = new Training();

    trainer.getTrainings().add(training);
    assertEquals(1, trainer.getTrainings().size());
    assertTrue(trainer.getTrainings().contains(training));

    trainer.getTrainings().remove(training);
    assertEquals(0, trainer.getTrainings().size());
    assertFalse(trainer.getTrainings().contains(training));
  }

  @Test
  void getTrainees_newInstance_emptySetInitialized() {
    Trainer newTrainer = new Trainer();
    assertNotNull(newTrainer.getTrainees());
    assertTrue(newTrainer.getTrainees().isEmpty());
  }

  @Test
  void getTrainings_newInstance_emptySetInitialized() {
    Trainer newTrainer = new Trainer();
    assertNotNull(newTrainer.getTrainings());
    assertTrue(newTrainer.getTrainings().isEmpty());
  }
}

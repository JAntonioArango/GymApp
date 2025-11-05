package com.epam.gymapp.entitiesTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.Training;
import com.epam.gymapp.entities.TrainingType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TrainingEntityTest {
  private Training training;
  private Trainee trainee;
  private Trainer trainer;
  private TrainingType trainingType;
  private LocalDate testDate;

  @BeforeEach
  void setUp() {
    training = new Training();
    trainee = new Trainee();
    trainer = new Trainer();
    trainingType = new TrainingType();
    testDate = LocalDate.of(2024, 1, 1);
  }

  @Test
  void constructor_noArgs_defaultNullValues() {
    assertNotNull(training);
    assertNull(training.getId());
    assertNull(training.getTrainee());
    assertNull(training.getTrainer());
    assertNull(training.getTrainingType());
    assertNull(training.getTrainingName());
    assertNull(training.getTrainingDate());
    assertNull(training.getTrainingDuration());
  }

  @Test
  void constructor_allArgs_fieldsSetCorrectly() {
    Training training =
        new Training(1L, trainee, trainer, trainingType, "Test Training", testDate, 60, true);

    assertEquals(1L, training.getId());
    assertEquals(trainee, training.getTrainee());
    assertEquals(trainer, training.getTrainer());
    assertEquals(trainingType, training.getTrainingType());
    assertEquals("Test Training", training.getTrainingName());
    assertEquals(testDate, training.getTrainingDate());
    assertEquals(60, training.getTrainingDuration());
  }

  @Test
  void setId_validId_idStored() {
    training.setId(1L);
    assertEquals(1L, training.getId());
  }

  @Test
  void setTrainee_validTrainee_traineeStored() {
    training.setTrainee(trainee);
    assertEquals(trainee, training.getTrainee());
  }

  @Test
  void setTrainer_validTrainer_trainerStored() {
    training.setTrainer(trainer);
    assertEquals(trainer, training.getTrainer());
  }

  @Test
  void setTrainingType_validType_trainingTypeStored() {
    training.setTrainingType(trainingType);
    assertEquals(trainingType, training.getTrainingType());
  }

  @Test
  void setTrainingName_validName_trainingNameStored() {
    String trainingName = "Test Training Session";
    training.setTrainingName(trainingName);
    assertEquals(trainingName, training.getTrainingName());
  }

  @Test
  void setTrainingDate_validDate_trainingDateStored() {
    training.setTrainingDate(testDate);
    assertEquals(testDate, training.getTrainingDate());
  }

  @Test
  void setTrainingDuration_validDuration_trainingDurationStored() {
    Integer duration = 45;
    training.setTrainingDuration(duration);
    assertEquals(duration, training.getTrainingDuration());
  }

  @Test
  void getTrainingDate_validDate_correctStringFormat() {
    LocalDate date = LocalDate.of(2024, 1, 1);
    training.setTrainingDate(date);
    assertEquals("2024-01-01", training.getTrainingDate().toString());
  }

  @Test
  void setTrainingDuration_positiveDuration_validDurationStored() {
    training.setTrainingDuration(30);
    assertTrue(training.getTrainingDuration() > 0);
  }

  @Test
  void constructor_allRequiredFields_requiredFieldsNotNull() {
    Training fullTraining =
        new Training(1L, trainee, trainer, trainingType, "Test Training", testDate, 60, true);

    assertNotNull(fullTraining.getTrainee(), "Trainee should not be null");
    assertNotNull(fullTraining.getTrainer(), "Trainer should not be null");
    assertNotNull(fullTraining.getTrainingType(), "Training type should not be null");
  }

  @Test
  void equals_sameContent_objectsEqual() {
    Training training1 =
        new Training(1L, trainee, trainer, trainingType, "Training 1", testDate, 60, true);
    Training training2 =
        new Training(1L, trainee, trainer, trainingType, "Training 1", testDate, 60, true);
    Training training3 =
        new Training(2L, trainee, trainer, trainingType, "Training 2", testDate, 45, true);

    assertThat(training1).usingRecursiveComparison().isEqualTo(training2);

    assertThat(training1).usingRecursiveComparison().isNotEqualTo(training3);
  }
}

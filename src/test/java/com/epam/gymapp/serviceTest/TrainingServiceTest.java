package com.epam.gymapp.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.dto.CreateTrainingDto;
import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.Training;
import com.epam.gymapp.repositories.TraineeRepo;
import com.epam.gymapp.repositories.TrainerRepo;
import com.epam.gymapp.repositories.TrainingRepo;
import com.epam.gymapp.services.TrainingService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {
  @Mock private TrainingRepo trainingRepo;
  @Mock private TraineeRepo traineeRepo;
  @Mock private TrainerRepo trainerRepo;

  @InjectMocks private TrainingService service;
  private CreateTrainingDto dto;

  @BeforeEach
  void init() {
    dto = new CreateTrainingDto("user1", "trainer1", "Session", LocalDate.now(), 30, true);
  }

  @Test
  void addTraining_nonExistentTrainee_apiExceptionThrown() {
    when(traineeRepo.findByUserUsername("user1")).thenReturn(Optional.empty());
    ApiException ex = assertThrows(ApiException.class, () -> service.addTraining(dto));
    assertTrue(ex.getMessage().contains("Trainee"));
  }

  @Test
  void addTraining_validTraineeAndTrainer_trainingSaved() {
    Trainee t = new Trainee();
    Trainer tr = new Trainer();
    when(traineeRepo.findByUserUsername("user1")).thenReturn(Optional.of(t));
    when(trainerRepo.findByUserUsername("trainer1")).thenReturn(Optional.of(tr));

    service.addTraining(dto);
    verify(trainingRepo).save(any(Training.class));
  }
}

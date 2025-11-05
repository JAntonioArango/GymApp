package com.epam.gymapp.apiTest.controllersTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.gymapp.api.controllers.TrainingController;
import com.epam.gymapp.api.dto.*;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.services.TrainingService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

  @Mock private TrainingService trainingService;

  @InjectMocks private TrainingController trainingController;

  @Mock private User mockUser;

  @Test
  void add_validTrainingDto_serviceCalledAndOkStatus() {

    CreateTrainingDto createTrainingDto =
        new CreateTrainingDto(
            "Daniela.Lopez123", "Sara.Maria123", "STRENGTH", LocalDate.of(2025, 5, 22), 15, true);

    ResponseEntity<Void> response = trainingController.add(createTrainingDto);

    verify(trainingService).addTraining(createTrainingDto);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  void traineeTrainings_validFilters_traineeTrainingsListReturned() {
    String username = "testUser";
    LocalDate from = LocalDate.now();
    LocalDate to = LocalDate.now().plusDays(7);
    String trainerName = "trainer1";
    String trainingType = "type1";
    int page = 0;
    int size = 20;

    List<TraineeTrainingDto> trainings = List.of(/* initialize with test data */ );
    Page<TraineeTrainingDto> trainingPage = new PageImpl<>(trainings);

    when(trainingService.traineeTrainings(
            eq(username),
            argThat(
                filter ->
                    filter.fromDate().equals(from)
                        && filter.toDate().equals(to)
                        && filter.trainerNameOrNull().equals(trainerName)
                        && filter.trainingType().equals(trainingType)),
            eq(PageRequest.of(page, size))))
        .thenReturn(trainingPage);

    ResponseEntity<List<TraineeTrainingDto>> result =
        trainingController.traineeTrainings(
            username, from, to, trainerName, trainingType, page, size);

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(trainings, result.getBody());
    verify(trainingService)
        .traineeTrainings(
            eq(username),
            any(TrainingService.TrainingFilter.class),
            eq(PageRequest.of(page, size)));
  }

  @Test
  void trainerTrainings_validFilters_trainerTrainingsListReturned() {
    String username = "testTrainer";
    LocalDate from = LocalDate.now();
    LocalDate to = LocalDate.now().plusDays(7);
    String traineeName = "trainee1";
    int page = 0;
    int size = 20;

    List<TrainerTrainingDto> trainings = List.of(/* initialize with test data */ );
    Page<TrainerTrainingDto> trainingPage = new PageImpl<>(trainings);

    when(trainingService.trainerTrainings(
            eq(username),
            any(TrainingService.TrainingFilter.class),
            eq(PageRequest.of(page, size))))
        .thenReturn(trainingPage);

    ResponseEntity<List<TrainerTrainingDto>> result =
        trainingController.trainerTrainings(username, from, to, traineeName, page, size);

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(trainings, result.getBody());
    verify(trainingService)
        .trainerTrainings(
            eq(username),
            any(TrainingService.TrainingFilter.class),
            eq(PageRequest.of(page, size)));
  }

  @Test
  void listTypes_noParameters_trainingTypesListReturned() {
    List<TrainingTypeDto> types = List.of(/* initialize with test data */ );
    when(trainingService.listTrainingTypes()).thenReturn(types);

    ResponseEntity<List<TrainingTypeDto>> result = trainingController.listTypes();

    assertNotNull(result);
    assertEquals(HttpStatus.OK, result.getStatusCode());
    assertEquals(types, result.getBody());
    verify(trainingService).listTrainingTypes();
  }
}

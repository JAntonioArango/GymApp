package com.epam.gymapp.apiTest.controllersTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.controllers.TraineeController;
import com.epam.gymapp.api.dto.*;
import com.epam.gymapp.entities.Specialization;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.services.TraineeService;
import com.epam.gymapp.services.TrainerService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

  @Mock private TraineeService traineeService;
  @Mock private TrainerService trainerService;

  @InjectMocks private TraineeController controller;

  private User caller;

  @BeforeEach
  void setUp() {
    caller = new User();
    caller.setUsername("admin");
  }

  @Test
  void create_validTraineeDto_createdStatusWithRegistrationCredentials() {
    CreateTraineeDto in =
        new CreateTraineeDto("John", "Doe", LocalDate.of(1990, 1, 1), "Address 1");
    TraineeRegistrationDto out = new TraineeRegistrationDto("john.doe-1234", "pass");
    when(traineeService.register(in)).thenReturn(out);

    ResponseEntity<TraineeRegistrationDto> resp = controller.create(in);

    assertEquals(HttpStatus.CREATED, resp.getStatusCode());
    assertEquals(out, resp.getBody());
    verify(traineeService).register(in);
  }

  @Test
  void delete_validUsername_serviceCalledAndOkStatus() {
    ResponseEntity<Void> resp = controller.delete("john");
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(traineeService).deleteByUsername("john");
  }

  @Test
  void setActive_validUsernameAndStatus_serviceCalledAndOkStatus() {
    ResponseEntity<Void> resp = controller.setActive("john", true);
    assertEquals(HttpStatus.OK, resp.getStatusCode());
    verify(traineeService).setActive("john", true);
  }

  @Test
  void replaceTrainers_validTrainersList_updatedTrainersListReturned() {
    List<String> trainerUsernames = List.of("sara.maria");
    UpdateTraineeTrainersDto body = new UpdateTraineeTrainersDto(trainerUsernames);
    List<TrainerShortDto> trainers =
        List.of(new TrainerShortDto("sara.maria", "Sara", "Maria", Specialization.BOXING));
    when(traineeService.replaceTrainers("john", trainerUsernames)).thenReturn(trainers);

    ResponseEntity<List<TrainerShortDto>> resp = controller.replaceTrainers("john", body);
    assertEquals(trainers, resp.getBody());
    verify(traineeService).replaceTrainers("john", trainerUsernames);
  }

  @Test
  void list_validPagination_traineeListReturned() {
    List<TraineeDto> dtos =
        List.of(new TraineeDto("john", "John", "Doe", LocalDate.of(1990, 1, 1), "Address", true));
    when(traineeService.list(PageRequest.of(0, 20))).thenReturn(new PageImpl<>(dtos));

    ResponseEntity<List<TraineeDto>> resp = controller.list(0, 20);

    assertEquals(dtos, resp.getBody());
  }
}

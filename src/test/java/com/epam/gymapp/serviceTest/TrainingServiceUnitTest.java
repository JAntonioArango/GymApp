package com.epam.gymapp.serviceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.dto.*;
import com.epam.gymapp.entities.*;
import com.epam.gymapp.repositories.*;
import com.epam.gymapp.services.TrainingService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class TrainingServiceUnitTest {

  @Mock private TrainingRepo trainingRepo;
  @Mock private TraineeRepo traineeRepo;
  @Mock private TrainerRepo trainerRepo;
  @Mock private TrainingTypeRepo typeRepo;

  @InjectMocks private TrainingService service;

  private CreateTrainingDto createDto;
  private Trainee mockTrainee;
  private Trainer mockTrainer;
  private TrainingType mockType;

  @BeforeEach
  void setUp() {
    createDto =
        new CreateTrainingDto(
            "traineeUser", "trainerUser", "YOGA", LocalDate.of(2025, 5, 1), 45, true);

    User traineeUser = new User();
    traineeUser.setUsername("traineeUser");
    mockTrainee = new Trainee();
    mockTrainee.setUser(traineeUser);

    User trainerUser = new User();
    trainerUser.setUsername("trainerUser");
    mockType = new TrainingType();
    mockType.setName(Specialization.YOGA);
    mockTrainer = new Trainer();
    mockTrainer.setUser(trainerUser);
    mockTrainer.setSpecialization(mockType);
  }

  @Test
  void addTraining_validTrainingDto_entitiesFetchedAndTrainingSaved() {
    when(traineeRepo.findByUserUsername("traineeUser")).thenReturn(Optional.of(mockTrainee));
    when(trainerRepo.findByUserUsername("trainerUser")).thenReturn(Optional.of(mockTrainer));

    service.addTraining(createDto);

    ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
    verify(trainingRepo).save(captor.capture());
    Training saved = captor.getValue();

    assertNull(saved.getId());
    assertThat(saved.getTrainee()).isEqualTo(mockTrainee);
    assertThat(saved.getTrainer()).isEqualTo(mockTrainer);
    assertThat(saved.getTrainingType()).isEqualTo(mockType);
    assertThat(saved.getTrainingName()).isEqualTo(createDto.trainingName());
    assertThat(saved.getTrainingDate()).isEqualTo(createDto.date());
    assertThat(saved.getTrainingDuration()).isEqualTo(createDto.duration());
  }

  @Test
  void listByTrainer_validTrainerUsername_entitiesMappedToDtos() {
    Training one =
        new Training(1L, mockTrainee, mockTrainer, mockType, "name1", LocalDate.now(), 30, true);
    when(trainingRepo.findByTrainerUserUsername("trainerUser")).thenReturn(List.of(one));

    List<TrainingDto> dtos = service.listByTrainer("trainerUser");

    assertThat(dtos).hasSize(1);
    TrainingDto dto = dtos.getFirst();
    assertEquals(one.getId(), dto.id());
    assertEquals("traineeUser", dto.traineeUsername());
    assertEquals("trainerUser", dto.trainerUsername());
    assertEquals("YOGA", dto.trainingType().toString());
    assertEquals("name1", dto.trainingName());
  }

  @Test
  void listByTrainee_validTraineeUsername_repositoryInvokedAndDtosReturned() {
    TrainingDto td =
        new TrainingDto(
            2L, "u", "t", "f", "l", Specialization.CARDIO, "n", LocalDate.now(), 20, true);
    when(trainingRepo.findTraineeTrainingsJPQL(eq("u"), any(), any(), any(), any()))
        .thenReturn(List.of(td));

    List<TrainingDto> out = service.listByTrainee("u", null);
    assertEquals(List.of(td), out);
    verify(trainingRepo).findTraineeTrainingsJPQL(eq("u"), any(), any(), any(), any());
  }

  @Test
  void listTrainingTypes_noParameters_entitiesConvertedToDtos() {
    TrainingType t1 = new TrainingType();
    t1.setId(10L);
    t1.setName(Specialization.CARDIO);
    when(typeRepo.findAll()).thenReturn(List.of(t1));

    List<TrainingTypeDto> types = service.listTrainingTypes();
    assertThat(types).containsExactly(new TrainingTypeDto(10L, Specialization.CARDIO));
  }

  @Test
  void trainingFilter_blankAndNullValues_blankValuesConvertedToNull() {
    TrainingService.TrainingFilter f =
        new TrainingService.TrainingFilter(null, null, "  ", "name", "");
    assertNull(f.trainerNameOrNull());
    assertEquals("name", f.traineeNameOrNull());
    assertNull(f.trainingTypeOrNull());
  }

  @Test
  void trainerTrainings_nullFilter_emptyFilterUsedAndPageReturned() {
    when(trainerRepo.findByUserUsername("trainer1")).thenReturn(Optional.of(mockTrainer));

    List<TrainerTrainingDto> data =
        List.of(
            new TrainerTrainingDto("name", LocalDate.now(), Specialization.YOGA, 30, "trainee"));
    Page<TrainerTrainingDto> page = new PageImpl<>(data);
    PageRequest pageReq = PageRequest.of(0, 10);

    when(trainingRepo.findTrainerTrainingRows(
            eq("trainer1"), isNull(), isNull(), isNull(), eq(pageReq)))
        .thenReturn(page);

    Page<TrainerTrainingDto> result = service.trainerTrainings("trainer1", null, pageReq);

    assertEquals(page, result);
    verify(trainingRepo)
        .findTrainerTrainingRows(eq("trainer1"), isNull(), isNull(), isNull(), eq(pageReq));
  }
}

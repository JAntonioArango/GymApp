package com.epam.gymapp.serviceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.dto.CreateTraineeDto;
import com.epam.gymapp.api.dto.TraineeDto;
import com.epam.gymapp.api.dto.TraineeProfileDto;
import com.epam.gymapp.api.dto.TraineeRegistrationDto;
import com.epam.gymapp.api.dto.TrainerShortDto;
import com.epam.gymapp.api.dto.UpdateTraineeDto;
import com.epam.gymapp.entities.Specialization;
import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.TrainingType;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.TraineeRepo;
import com.epam.gymapp.repositories.TrainerRepo;
import com.epam.gymapp.services.TraineeService;
import com.epam.gymapp.utils.CredentialGenerator;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

  @Mock private TraineeRepo traineeRepo;
  @Mock private CredentialGenerator creds;
  @Mock private TrainerRepo trainerRepo;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private TraineeService service;

  private CreateTraineeDto dto;

  @BeforeEach
  void init() {
    dto = new CreateTraineeDto("John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St");

    lenient().when(passwordEncoder.encode(any())).thenReturn("$2a$dummyhash");

    User savedUser = new User();
    savedUser.setUsername("john.doe-abc123");
    savedUser.setPassword("plainPass");

    Trainee savedTrainee = new Trainee();
    savedTrainee.setUser(savedUser);
    savedTrainee.setDateOfBirth(dto.dateOfBirth());
    savedTrainee.setAddress(dto.address());
  }

  @Test
  void register_validTraineeDto_registrationDtoWithRawPassword() {
    when(creds.randomPassword()).thenReturn("rawPass123");
    when(creds.buildUniqueUsername("John", "Doe")).thenReturn("john.doe");
    when(traineeRepo.save(any(Trainee.class)))
        .thenAnswer(
            inv -> {
              Trainee t = inv.getArgument(0);
              t.getUser().setUsername("john.doe");
              return t;
            });

    TraineeRegistrationDto result = service.register(dto);

    assertEquals("john.doe", result.username());
    assertEquals("rawPass123", result.password());
    verify(creds).randomPassword();
  }

  @Test
  void findProfile_existingTraineeWithTrainers_profileDtoWithMappedTrainers() {
    User traineeUser = new User();
    traineeUser.setUsername("trainee1");
    traineeUser.setFirstName("John");
    traineeUser.setLastName("Doe");
    traineeUser.setActive(true);

    User trainerUser = new User();
    trainerUser.setUsername("trainer1");
    trainerUser.setFirstName("Jane");
    trainerUser.setLastName("Smith");

    Trainer trainer = new Trainer();
    trainer.setUser(trainerUser);
    trainer.setSpecialization(new TrainingType(1L, Specialization.CARDIO, null));

    Trainee trainee = new Trainee();
    trainee.setUser(traineeUser);
    trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
    trainee.setAddress("123 Main St");
    trainee.setTrainers(Set.of(trainer));

    when(traineeRepo.findByUserUsername("trainee1")).thenReturn(Optional.of(trainee));

    TraineeProfileDto result = service.findProfile("trainee1");

    assertEquals(1, result.trainers().size());
    assertEquals("trainer1", result.trainers().getFirst().username());
    assertEquals("Jane", result.trainers().getFirst().firstName());
    assertEquals("Smith", result.trainers().getFirst().lastName());
    assertEquals(Specialization.CARDIO, result.trainers().getFirst().specialization());
  }

  @Test
  void findProfile_existingTrainee_profileDtoMappedAndReturned() {
    Trainee t = new Trainee();
    User u = new User();
    u.setUsername("u");
    u.setFirstName("F");
    u.setLastName("L");
    u.setActive(true);
    t.setUser(u);
    t.setDateOfBirth(LocalDate.now());
    t.setAddress("Addr");
    when(traineeRepo.findByUserUsername("u")).thenReturn(Optional.of(t));

    TraineeProfileDto pd = service.findProfile("u");
    assertEquals("u", pd.userName());
    assertTrue(pd.trainers().isEmpty());
  }

  @Test
  void updateProfile_existingUsernameConflict_throwsDuplicateException() {
    Trainee existingTrainee = new Trainee();
    existingTrainee.setId(1L);
    User existingUser = new User();
    existingUser.setUsername("john.doe");
    existingUser.setFirstName("John");
    existingUser.setLastName("Doe");
    existingUser.setActive(true);
    existingTrainee.setUser(existingUser);

    Trainee foundTrainee = new Trainee();
    foundTrainee.setId(2L);
    User foundUser = new User();
    foundUser.setUsername("existing.user");
    foundTrainee.setUser(foundUser);

    UpdateTraineeDto updateDto =
        new UpdateTraineeDto(
            "existing.user", "John", "Doe", LocalDate.of(1990, 1, 1), "123 Main St", true);

    when(traineeRepo.findByUserUsername("john.doe")).thenReturn(Optional.of(existingTrainee));
    when(traineeRepo.findByUserUsername("existing.user")).thenReturn(Optional.of(foundTrainee));

    ApiException ex =
        assertThrows(ApiException.class, () -> service.updateProfile("john.doe", updateDto));
    assertEquals(HttpStatus.CONFLICT, ex.getStatus());
  }

  @Test
  void updateProfile_validUpdateData_profileModifiedAndReturned() {
    UpdateTraineeDto upd =
        new UpdateTraineeDto("AliceT", "Alice", "Smith", null, "New Addr", false);
    Trainee t = new Trainee();
    User u = new User();
    u.setFirstName("Old");
    u.setLastName("Old");
    t.setUser(u);
    when(traineeRepo.findByUserUsername("alice")).thenReturn(Optional.of(t));

    TraineeProfileDto profile = service.updateProfile("alice", upd);

    assertEquals("Alice", profile.firstName());
    assertEquals("Smith", profile.lastName());
    assertEquals("New Addr", profile.address());
    assertFalse(profile.isActive());
  }

  @Test
  void deleteByUsername_existingTrainee_traineeDetachedAndDeleted() {
    Trainee t = spy(new Trainee());
    when(traineeRepo.findByUserUsername("u")).thenReturn(Optional.of(t));

    service.deleteByUsername("u");

    verify(traineeRepo).delete(t);
  }

  @Test
  void setActive_validStatusChange_activeStatusToggledAndDtoReturned() {
    Trainee t = new Trainee();
    User u = new User();
    u.setActive(false);
    t.setUser(u);
    when(traineeRepo.findByUserUsername("u")).thenReturn(Optional.of(t));

    TraineeDto dto = service.setActive("u", true);
    assertTrue(dto.active());
  }

  @Test
  void replaceTrainers_validTrainerUsernames_trainersReplacedAndShortDtosReturned() {
    Trainee t = new Trainee();
    when(traineeRepo.findByUserUsername("u")).thenReturn(Optional.of(t));
    Trainer tr1 = new Trainer();
    User u1 = new User();
    u1.setUsername("a");
    u1.setFirstName("A");
    u1.setLastName("B");
    tr1.setUser(u1);
    tr1.setSpecialization(new TrainingType());
    when(trainerRepo.findByUserUsernameIn(List.of("a"))).thenReturn(List.of(tr1));

    var out = service.replaceTrainers("u", List.of("a"));
    assertThat(out).hasSize(1).extracting(TrainerShortDto::username).containsExactly("a");
  }

  @Test
  void replaceTrainers_nonExistentTrainers_apiExceptionThrown() {
    Trainee trainee = new Trainee();
    when(traineeRepo.findByUserUsername("u")).thenReturn(Optional.of(trainee));

    List<String> trainerUsernames = List.of("a", "b");
    when(trainerRepo.findByUserUsernameIn(trainerUsernames)).thenReturn(List.of());

    ApiException ex =
        assertThrows(ApiException.class, () -> service.replaceTrainers("u", trainerUsernames));

    assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    assertTrue(ex.getMessage().contains("Trainer IDs"));
    assertTrue(ex.getMessage().contains(String.join(",", trainerUsernames)));
  }
}

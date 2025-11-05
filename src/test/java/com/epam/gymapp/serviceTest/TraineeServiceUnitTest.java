package com.epam.gymapp.serviceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.dto.*;
import com.epam.gymapp.entities.*;
import com.epam.gymapp.repositories.*;
import com.epam.gymapp.services.TraineeService;
import com.epam.gymapp.utils.CredentialGenerator;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TraineeServiceUnitTest {

  @Mock private TraineeRepo traineeRepo;
  @Mock private CredentialGenerator creds;
  @Mock private TrainerRepo trainerRepo;
  @Mock private PasswordEncoder encoder;

  @InjectMocks private TraineeService service;

  @BeforeEach
  void setUp() {
    lenient().when(encoder.encode(anyString())).thenReturn("$2a$hash");

    service = new TraineeService(traineeRepo, creds, trainerRepo, encoder);
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

    // Verify the exception carries the expected semantics
    assertEquals(org.springframework.http.HttpStatus.NOT_FOUND, ex.getStatus());
    assertTrue(ex.getMessage().contains("Trainer IDs"));
    assertTrue(ex.getMessage().contains(String.join(",", trainerUsernames)));
  }
}

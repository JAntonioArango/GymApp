package com.epam.gymapp.serviceTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.dto.CreateTrainerDto;
import com.epam.gymapp.api.dto.TrainerDto;
import com.epam.gymapp.api.dto.TrainerProfileDto;
import com.epam.gymapp.api.dto.TrainerRegistrationDto;
import com.epam.gymapp.api.dto.TrainerShortDto;
import com.epam.gymapp.api.dto.UpdateTrainerDto;
import com.epam.gymapp.entities.Specialization;
import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.TrainingType;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.TraineeRepo;
import com.epam.gymapp.repositories.TrainerRepo;
import com.epam.gymapp.repositories.TrainingTypeRepo;
import com.epam.gymapp.services.TrainerService;
import com.epam.gymapp.utils.CredentialGenerator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

  @Mock private TrainerRepo trainerRepo;
  @Mock private TrainingTypeRepo typeRepo;
  @Mock private CredentialGenerator creds;
  @Mock private TraineeRepo traineeRepo;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private TrainerService service;

  private CreateTrainerDto dto;
  private TrainingType mockType;
  private User savedUser;
  private Trainer savedTrainer;

  @BeforeEach
  void init() {
    dto = new CreateTrainerDto("Alice", "Smith", Specialization.CARDIO);

    mockType = new TrainingType();
    mockType.setName(Specialization.YOGA);

    savedUser = new User();
    savedUser.setUsername("john.doe-xyz");
    savedUser.setPassword("pwd");
    savedTrainer = new Trainer();
    savedTrainer.setUser(savedUser);
    savedTrainer.setSpecialization(mockType);
  }

  @Test
  void register_validTrainerDto_userAndTrainerCreatedWithEncodedPassword() {
    when(creds.randomPassword()).thenReturn("Pass123");
    when(creds.buildUniqueUsername("Alice", "Smith")).thenReturn("asmith");
    when(passwordEncoder.encode("Pass123")).thenReturn("encodedPass123");
    when(typeRepo.findByName(Specialization.CARDIO))
        .thenReturn(Optional.of(new TrainingType(1L, Specialization.CARDIO, null)));
    when(trainerRepo.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

    TrainerRegistrationDto result = service.register(dto);

    assertEquals("asmith", result.username());
    assertEquals("Pass123", result.password());
    verify(creds).randomPassword();
    verify(creds).buildUniqueUsername("Alice", "Smith");
    verify(passwordEncoder).encode("Pass123");
    verify(trainerRepo).save(any(Trainer.class));
  }

  @Test
  void register_validTrainerDto_userFieldsSetCorrectly() {
    when(creds.randomPassword()).thenReturn("Pass123");
    when(creds.buildUniqueUsername("Alice", "Smith")).thenReturn("asmith");
    when(passwordEncoder.encode("Pass123")).thenReturn("encodedPass123");
    when(typeRepo.findByName(Specialization.CARDIO))
        .thenReturn(Optional.of(new TrainingType(1L, Specialization.CARDIO, null)));
    when(trainerRepo.save(any(Trainer.class)))
        .thenAnswer(
            inv -> {
              Trainer t = inv.getArgument(0);
              User u = t.getUser();
              assertEquals("Alice", u.getFirstName());
              assertEquals("Smith", u.getLastName());
              assertEquals("asmith", u.getUsername());
              assertEquals("encodedPass123", u.getPassword());
              assertTrue(u.isActive());
              return t;
            });

    service.register(dto);

    verify(trainerRepo).save(any(Trainer.class));
  }

  @Test
  void findByUsername_existingAndNonExistingUsername_dtoReturnedOrExceptionThrown() {
    when(trainerRepo.findByUserUsername("u")).thenReturn(Optional.of(savedTrainer));
    TrainerDto result = service.findByUsername("u");
    assertNotEquals("u", result.username());

    when(trainerRepo.findByUserUsername("x")).thenReturn(Optional.empty());
    assertThrows(ApiException.class, () -> service.findByUsername("x"));
  }

  @Test
  void updateProfile_existingUsernameConflict_runtimeExceptionThrown() {
    UpdateTrainerDto updateDto =
        new UpdateTrainerDto("existinguser", "Alice", "Smith", Specialization.CARDIO, true);

    Trainer currentTrainer = new Trainer();
    currentTrainer.setId(1L);
    currentTrainer.setUser(new User());

    Trainer existingTrainer = new Trainer();
    existingTrainer.setId(2L);

    when(trainerRepo.findByUserUsername("alice.smith")).thenReturn(Optional.of(currentTrainer));
    when(trainerRepo.findByUserUsername("existinguser")).thenReturn(Optional.of(existingTrainer));

    assertThrows(RuntimeException.class, () -> service.updateProfile("alice.smith", updateDto));
  }

  @Test
  void updateProfile_validUpdateData_profileModifiedAndReturned() {
    UpdateTrainerDto upd =
        new UpdateTrainerDto("AliceTrainer", "Alice", "Smith", Specialization.BOXING, false);
    savedTrainer.getUser().setFirstName("Old");
    savedTrainer.getUser().setLastName("Old");
    savedTrainer.getUser().setActive(true);
    when(trainerRepo.findByUserUsername("john")).thenReturn(Optional.of(savedTrainer));

    TrainerProfileDto pd = service.updateProfile("john", upd);
    assertEquals("Alice", pd.firstName());
    assertEquals("Smith", pd.lastName());
    assertFalse(pd.isActive());
  }

  @Test
  void changePassword_validAndInvalidCredentials_passwordChangedOrExceptionThrown() {
    when(trainerRepo.existsByUserUsernameAndUserPassword("u", "old")).thenReturn(false);
    assertThrows(ApiException.class, () -> service.changePassword("u", "old", "new"));

    when(trainerRepo.existsByUserUsernameAndUserPassword("u", "old")).thenReturn(true);
    when(trainerRepo.findByUserUsername("u")).thenReturn(Optional.of(savedTrainer));
    service.changePassword("u", "old", "new");
    assertEquals("new", savedTrainer.getUser().getPassword());
  }

  @Test
  void setActive_validStatusChange_activeStatusToggledAndDtoReturned() {
    savedTrainer.getUser().setActive(false);
    when(trainerRepo.findByUserUsername("u")).thenReturn(Optional.of(savedTrainer));
    TrainerDto result = service.setActive("u", true);
    assertTrue(result.active());
  }

  @Test
  void list_validPagination_pagedTrainerDtosReturned() {
    savedTrainer.setId(5L);
    when(trainerRepo.findAll(PageRequest.of(0, 2)))
        .thenReturn(new PageImpl<>(List.of(savedTrainer)));

    var page = service.list(PageRequest.of(0, 2));
    assertEquals(1, page.getTotalElements());
    assertEquals(5L, page.getContent().getFirst().id());
  }

  @Test
  void findProfile_trainerWithTrainees_profileDtoWithMappedTrainees() {
    User trainerUser = new User();
    trainerUser.setUsername("trainer1");
    trainerUser.setFirstName("John");
    trainerUser.setLastName("Doe");
    trainerUser.setActive(true);

    User traineeUser = new User();
    traineeUser.setUsername("trainee1");
    traineeUser.setFirstName("Jane");
    traineeUser.setLastName("Smith");

    Trainee trainee = new Trainee();
    trainee.setUser(traineeUser);

    Trainer trainer = new Trainer();
    trainer.setUser(trainerUser);
    trainer.setSpecialization(new TrainingType(1L, Specialization.CARDIO, null));
    trainer.setTrainees(Set.of(trainee));

    when(trainerRepo.findByUserUsername("trainer1")).thenReturn(Optional.of(trainer));

    TrainerProfileDto result = service.findProfile("trainer1");

    assertEquals(1, result.trainees().size());
    assertEquals("trainee1", result.trainees().getFirst().username());
    assertEquals("Jane", result.trainees().getFirst().firstName());
    assertEquals("Smith", result.trainees().getFirst().lastName());
  }

  @Test
  void findProfile_existingTrainer_profileDtoMappedAndReturned() {
    when(trainerRepo.findByUserUsername("u")).thenReturn(Optional.of(savedTrainer));
    TrainerProfileDto pd = service.findProfile("u");
    assertNotEquals("u", pd.username());
    assertThat(pd.trainees()).isEmpty();
  }

  @Test
  void unassignedActiveTrainers_traineeWithAssignedTrainers_unassignedActiveTrainersFiltered() {
    when(traineeRepo.findByUserUsername("trainee")).thenReturn(Optional.of(new Trainee()));

    Trainer assigned = new Trainer();
    assigned.setId(1L);
    User assignedUser = new User();
    assignedUser.setUsername("assigned");
    assignedUser.setActive(true);
    assigned.setUser(assignedUser);
    when(trainerRepo.findByTraineesUserUsername("trainee")).thenReturn(List.of(assigned));

    Trainer free = new Trainer();
    free.setId(2L);
    User freeUser = new User();
    freeUser.setUsername("free");
    freeUser.setFirstName("F");
    freeUser.setLastName("L");
    freeUser.setActive(true);
    free.setUser(freeUser);
    free.setSpecialization(mockType);
    when(trainerRepo.findAll()).thenReturn(List.of(assigned, free));

    List<TrainerShortDto> out = service.unassignedActiveTrainers("trainee");
    assertEquals(1, out.size());
    assertEquals("free", out.getFirst().username());
  }
}

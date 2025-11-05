package com.epam.gymapp.serviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.dto.CreateTrainerDto;
import com.epam.gymapp.api.dto.TrainerDto;
import com.epam.gymapp.api.dto.TrainerProfileDto;
import com.epam.gymapp.api.dto.TrainerRegistrationDto;
import com.epam.gymapp.api.dto.UpdateTrainerDto;
import com.epam.gymapp.entities.Specialization;
import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.TrainingType;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.TrainerRepo;
import com.epam.gymapp.repositories.TrainingTypeRepo;
import com.epam.gymapp.services.TrainerService;
import com.epam.gymapp.utils.CredentialGenerator;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

  @Mock private TrainerRepo trainerRepo;
  @Mock private TrainingTypeRepo typeRepo;
  @Mock private CredentialGenerator creds;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private TrainerService service;
  private CreateTrainerDto dto;

  @BeforeEach
  void init() {
    dto = new CreateTrainerDto("Alice", "Smith", Specialization.CARDIO);
  }

  @Test
  void createProfile_validTrainerDto_trainingTypeLoadedAndTrainerDtoReturned() {
    when(creds.buildUniqueUsername("Alice", "Smith")).thenReturn("asmith");
    when(typeRepo.findByName(Specialization.CARDIO))
        .thenReturn(Optional.of(new TrainingType(1L, Specialization.CARDIO, null)));
    when(trainerRepo.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

    TrainerDto result = service.createProfile(dto);

    assertNotNull(result);
    assertEquals("asmith", result.username());
    assertEquals("CARDIO", result.specialization().toString());
    verify(creds).buildUniqueUsername("Alice", "Smith");
    verify(trainerRepo).save(any(Trainer.class));
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
}

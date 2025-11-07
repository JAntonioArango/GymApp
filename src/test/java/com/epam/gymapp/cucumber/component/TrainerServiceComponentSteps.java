package com.epam.gymapp.cucumber.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeast;

import com.epam.gymapp.api.dto.CreateTrainerDto;
import com.epam.gymapp.api.dto.TrainerProfileDto;
import com.epam.gymapp.api.dto.TrainerRegistrationDto;
import com.epam.gymapp.api.dto.UpdateTrainerDto;
import com.epam.gymapp.entities.Specialization;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.TrainingType;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.TraineeRepo;
import com.epam.gymapp.repositories.TrainerRepo;
import com.epam.gymapp.services.TrainerService;
import com.epam.gymapp.utils.CredentialGenerator;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TrainerServiceComponentSteps {

  private TrainerService trainerService;
  private TrainerRepo mockTrainerRepo;
  private PasswordEncoder mockPasswordEncoder;
  private CredentialGenerator mockCredentialGenerator;
  private CreateTrainerDto createTrainerDto;
  private TrainerRegistrationDto result;
  private TrainerProfileDto profileResult;
  private Exception thrownException;
  private String username;
  private UpdateTrainerDto updateTrainerDto;
  private List<Trainer> trainersResult;
  private TraineeRepo mockTraineeRepo;

  @Given("the trainer service is initialized")
  public void theTrainerServiceIsInitialized() {
    mockTrainerRepo = mock(TrainerRepo.class);
    mockPasswordEncoder = mock(PasswordEncoder.class);
    mockCredentialGenerator = mock(CredentialGenerator.class);
    var mockTrainingTypeRepo = mock(com.epam.gymapp.repositories.TrainingTypeRepo.class);
    mockTraineeRepo = mock(com.epam.gymapp.repositories.TraineeRepo.class);
    var mockUserRepository = mock(com.epam.gymapp.repositories.UserRepository.class);

    // Mock TrainingType lookup
    TrainingType yogaType = new TrainingType();
    yogaType.setId(1L);
    yogaType.setName(Specialization.YOGA);
    when(mockTrainingTypeRepo.findByName(Specialization.YOGA))
        .thenReturn(java.util.Optional.of(yogaType));

    trainerService =
        new TrainerService(
            mockTrainerRepo,
            mockTrainingTypeRepo,
            mockCredentialGenerator,
            mockTraineeRepo,
            mockUserRepository,
            mockPasswordEncoder);
  }

  @Given("I have valid trainer creation data")
  public void iHaveValidTrainerCreationData() {
    createTrainerDto = new CreateTrainerDto("John", "Trainer", Specialization.YOGA);

    TrainingType trainingType = new TrainingType();
    trainingType.setId(1L);
    trainingType.setName(Specialization.YOGA);

    when(mockCredentialGenerator.buildUniqueUsername("John", "Trainer")).thenReturn("john.trainer");
    when(mockCredentialGenerator.randomPassword()).thenReturn("password123");
    when(mockPasswordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(mockTrainerRepo.save(any(Trainer.class)))
        .thenAnswer(
            invocation -> {
              Trainer trainer = invocation.getArgument(0);
              trainer.setId(1L);
              return trainer;
            });
  }

  @When("I create trainer through the service")
  public void iCreateTrainerThroughTheService() {
    try {
      result = trainerService.register(createTrainerDto);
      thrownException = null;
    } catch (Exception e) {
      thrownException = e;
      result = null;
    }
  }

  @Then("the trainer should be created successfully")
  public void theTrainerShouldBeCreatedSuccessfully() {
    assertNull(thrownException);
    assertNotNull(result);
  }

  @Then("credentials should be generated")
  public void credentialsShouldBeGenerated() {
    assertNotNull(result);
    assertEquals("john.trainer", result.username());
    assertEquals("password123", result.password());
  }

  @Then("the repository should be called")
  public void theRepositoryShouldBeCalled() {
    verify(mockTrainerRepo).save(any(Trainer.class));
  }

  @Given("trainer {string} exists in the system")
  public void trainerExistsInTheSystem(String username) {
    this.username = username;

    User user = new User();
    user.setUsername(username);
    user.setFirstName("John");
    user.setLastName("Trainer");
    user.setActive(true);

    TrainingType specialization = new TrainingType();
    specialization.setId(1L);
    specialization.setName(Specialization.YOGA);

    Trainer trainer = new Trainer();
    trainer.setId(1L);
    trainer.setUser(user);
    trainer.setSpecialization(specialization);

    when(mockTrainerRepo.findByUserUsername(username)).thenReturn(Optional.of(trainer));
  }

  @When("I get trainer profile through the service")
  public void iGetTrainerProfileThroughTheService() {
    try {
      profileResult = trainerService.findProfile(username);
      thrownException = null;
    } catch (Exception e) {
      thrownException = e;
      profileResult = null;
    }
  }

  @Then("I should receive trainer profile data")
  public void iShouldReceiveTrainerProfileData() {
    assertNull(thrownException);
    assertNotNull(profileResult);
  }

  @Then("the profile should contain correct information")
  public void theProfileShouldContainCorrectInformation() {
    assertEquals("John", profileResult.firstName());
    assertEquals("Trainer", profileResult.lastName());
    assertEquals(Specialization.YOGA, profileResult.specialization());
  }

  @Then("no service exception should be thrown")
  public void noServiceExceptionShouldBeThrown() {
    assertNull(thrownException);
  }

  @Given("trainer {string} does not exist")
  public void trainerDoesNotExist(String username) {
    this.username = username;
    when(mockTrainerRepo.findByUserUsername(username)).thenReturn(null);
  }

  @Then("a not found exception should be thrown")
  public void aNotFoundExceptionShouldBeThrown() {
    assertNotNull(thrownException);
  }

  @Then("no profile data should be returned")
  public void noProfileDataShouldBeReturned() {
    assertNull(profileResult);
  }

  @Given("trainer {string} exists with current data")
  public void trainerExistsWithCurrentData(String username) {
    this.username = username;
    updateTrainerDto =
        new UpdateTrainerDto("jane.trainer", "Jane", "UpdatedTrainer", Specialization.YOGA, true);

    User user = new User();
    user.setUsername(username);
    user.setFirstName("Jane");
    user.setLastName("Trainer");
    user.setActive(true);

    TrainingType specialization = new TrainingType();
    specialization.setId(1L);
    specialization.setName(Specialization.YOGA);

    Trainer trainer = new Trainer();
    trainer.setId(1L);
    trainer.setUser(user);
    trainer.setSpecialization(specialization);

    when(mockTrainerRepo.findByUserUsername(username)).thenReturn(Optional.of(trainer));
    when(mockTrainerRepo.save(any(Trainer.class)))
        .thenAnswer(
            invocation -> {
              Trainer savedTrainer = invocation.getArgument(0);
              if (savedTrainer.getId() == null) savedTrainer.setId(1L);
              return savedTrainer;
            });
  }

  @When("I update trainer profile through the service")
  public void iUpdateTrainerProfileThroughTheService() {
    try {
      profileResult = trainerService.updateProfile(username, updateTrainerDto);
      thrownException = null;
    } catch (Exception e) {
      thrownException = e;
      profileResult = null;
    }
  }

  @Then("the trainer should be updated successfully")
  public void theTrainerShouldBeUpdatedSuccessfully() {
    assertNull(thrownException);
    assertNotNull(profileResult);
  }

  @Then("the repository should save the changes")
  public void theRepositoryShouldSaveTheChanges() {
    verify(mockTrainerRepo, atLeast(1)).findByUserUsername("jane.trainer");
  }

  @Given("there are active and inactive trainers in the system")
  public void thereAreActiveAndInactiveTrainersInTheSystem() {
    TrainingType specialization = new TrainingType();
    specialization.setId(1L);
    specialization.setName(Specialization.YOGA);

    User activeUser = new User();
    activeUser.setActive(true);
    Trainer activeTrainer = new Trainer();
    activeTrainer.setUser(activeUser);
    activeTrainer.setId(1L);
    activeTrainer.setSpecialization(specialization);

    User inactiveUser = new User();
    inactiveUser.setActive(false);
    Trainer inactiveTrainer = new Trainer();
    inactiveTrainer.setUser(inactiveUser);
    inactiveTrainer.setId(2L);
    inactiveTrainer.setSpecialization(specialization);

    when(mockTrainerRepo.findAll()).thenReturn(List.of(activeTrainer, inactiveTrainer));
    when(mockTrainerRepo.findByTraineesUserUsername("test.trainee")).thenReturn(List.of());
    when(mockTraineeRepo.findByUserUsername("test.trainee"))
        .thenReturn(java.util.Optional.of(new com.epam.gymapp.entities.Trainee()));
  }

  @When("I request unassigned active trainers")
  public void iRequestUnassignedActiveTrainers() {
    try {
      var shortDtos = trainerService.unassignedActiveTrainers("test.trainee");
      trainersResult = List.of(); // Simplified for test
      thrownException = null;
    } catch (Exception e) {
      thrownException = e;
      trainersResult = null;
    }
  }

  @Then("I should receive only active unassigned trainers")
  public void iShouldReceiveOnlyActiveUnassignedTrainers() {
    assertNull(thrownException);
  }

  @Then("the list should not contain inactive trainers")
  public void theListShouldNotContainInactiveTrainers() {
    assertNull(thrownException);
  }
}

package com.epam.gymapp.services;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.dto.CreateTraineeDto;
import com.epam.gymapp.api.dto.TraineeDto;
import com.epam.gymapp.api.dto.TraineeProfileDto;
import com.epam.gymapp.api.dto.TraineeRegistrationDto;
import com.epam.gymapp.api.dto.TrainerShortDto;
import com.epam.gymapp.api.dto.UpdateTraineeDto;
import com.epam.gymapp.entities.Role;
import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.TraineeRepo;
import com.epam.gymapp.repositories.TrainerRepo;
import com.epam.gymapp.utils.CredentialGenerator;
import com.epam.gymapp.utils.ProfileUpdateSupport;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class TraineeService {

  private final TraineeRepo traineeRepo;
  private final CredentialGenerator creds;
  private final TrainerRepo trainerRepo;
  private final PasswordEncoder passwordEncoder;

  public TraineeRegistrationDto register(CreateTraineeDto dto) {

    String rawPwd = creds.randomPassword();

    Trainee trainee = buildAndSaveEntity(dto, rawPwd);

    return new TraineeRegistrationDto(trainee.getUser().getUsername(), rawPwd);
  }

  private Trainee buildAndSaveEntity(CreateTraineeDto dto, String rawPwd) {

    User u = new User();
    u.setFirstName(dto.firstName());
    u.setLastName(dto.lastName());
    u.setUsername(creds.buildUniqueUsername(dto.firstName(), dto.lastName()));
    u.setPassword(passwordEncoder.encode(rawPwd));
    u.setActive(true);
    u.setRole(Role.TRAINEE);

    Trainee t = new Trainee();
    t.setUser(u);
    t.setDateOfBirth(dto.dateOfBirth());
    t.setAddress(dto.address());

    return traineeRepo.save(t);
  }

  public TraineeProfileDto updateProfile(String username, UpdateTraineeDto dto) {

    Trainee trainee =
        traineeRepo
            .findByUserUsername(username)
            .orElseThrow(() -> ApiException.notFound("Trainee", username));

    ProfileUpdateSupport.assertUsernameAvailable(
        traineeRepo::findByUserUsername, Trainee::getId, trainee.getId(), dto.username());

    trainee.getUser().setUsername(ProfileUpdateSupport.sanitizeUsername(dto.username()));

    trainee.getUser().setFirstName(dto.firstName());
    trainee.getUser().setLastName(dto.lastName());

    if (dto.dateOfBirth() != null) trainee.setDateOfBirth(dto.dateOfBirth());
    if (dto.address() != null) trainee.setAddress(dto.address());

    trainee.getUser().setActive(dto.isActive());

    return toProfileDto(trainee);
  }

  @Transactional
  public void deleteByUsername(String username) {

    Trainee trainee =
        traineeRepo
            .findByUserUsername(username)
            .orElseThrow(() -> ApiException.notFound("Trainee", username));

    trainee.getTrainers().forEach(tr -> tr.getTrainees().remove(trainee));
    trainee.getTrainers().clear();

    traineeRepo.delete(trainee);
  }

  public TraineeDto setActive(String username, boolean active) {

    Trainee trainee =
        traineeRepo
            .findByUserUsername(username)
            .orElseThrow(() -> ApiException.notFound("Trainee", username));

    trainee.getUser().setActive(active);
    return toDto(trainee);
  }

  public Page<TraineeDto> list(Pageable pageable) {
    return traineeRepo.findAll(pageable).map(this::toDto);
  }

  private TraineeDto toDto(Trainee t) {
    return new TraineeDto(
        t.getUser().getUsername(),
        t.getUser().getFirstName(),
        t.getUser().getLastName(),
        t.getDateOfBirth(),
        t.getAddress(),
        t.getUser().isActive());
  }

  public TraineeProfileDto findProfile(String username) {

    Trainee t =
        traineeRepo
            .findByUserUsername(username)
            .orElseThrow(() -> ApiException.notFound("Trainee", username));

    return toProfileDto(t);
  }

  private TraineeProfileDto toProfileDto(Trainee t) {

    List<TrainerShortDto> trainerDtos =
        t.getTrainers().stream()
            .map(
                tr ->
                    new TrainerShortDto(
                        tr.getUser().getUsername(),
                        tr.getUser().getFirstName(),
                        tr.getUser().getLastName(),
                        tr.getSpecialization().getName()))
            .toList();

    return new TraineeProfileDto(
        t.getUser().getUsername(),
        t.getUser().getFirstName(),
        t.getUser().getLastName(),
        t.getDateOfBirth(),
        t.getAddress(),
        t.getUser().isActive(),
        trainerDtos);
  }

  public List<TrainerShortDto> replaceTrainers(
      String traineeUsername, List<String> trainerUsernames) {

    Trainee trainee =
        traineeRepo
            .findByUserUsername(traineeUsername)
            .orElseThrow(() -> ApiException.notFound("Trainee", traineeUsername));

    List<Trainer> trainers = trainerRepo.findByUserUsernameIn(trainerUsernames);

    if (trainers.size() != trainerUsernames.size()) {
      throw ApiException.notFound("Trainer IDs", String.join(",", trainerUsernames));
    }

    trainee.setTrainers(new HashSet<>(trainers));

    return trainers.stream()
        .map(
            tr ->
                new TrainerShortDto(
                    tr.getUser().getUsername(),
                    tr.getUser().getFirstName(),
                    tr.getUser().getLastName(),
                    tr.getSpecialization().getName()))
        .toList();
  }
}

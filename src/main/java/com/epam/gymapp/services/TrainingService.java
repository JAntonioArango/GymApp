package com.epam.gymapp.services;

import com.epam.gymapp.api.advice.ApiException;
import com.epam.gymapp.api.dto.CreateTrainingDto;
import com.epam.gymapp.api.dto.TraineeTrainingDto;
import com.epam.gymapp.api.dto.TrainerTrainingDto;
import com.epam.gymapp.api.dto.TrainingDto;
import com.epam.gymapp.api.dto.TrainingTypeDto;
import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.Trainer;
import com.epam.gymapp.entities.Training;
import com.epam.gymapp.repositories.TraineeRepo;
import com.epam.gymapp.repositories.TrainerRepo;
import com.epam.gymapp.repositories.TrainingRepo;
import com.epam.gymapp.repositories.TrainingTypeRepo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingService {

  private final TrainingRepo trainingRepo;
  private final TraineeRepo traineeRepo;
  private final TrainerRepo trainerRepo;
  private final TrainingTypeRepo typeRepo;

  @Transactional
  public void addTraining(CreateTrainingDto dto) {

    Trainee trainee =
        traineeRepo
            .findByUserUsername(dto.traineeUsername())
            .orElseThrow(() -> ApiException.notFound("Trainee", dto.traineeUsername()));

    Trainer trainer =
        trainerRepo
            .findByUserUsername(dto.trainerUsername())
            .orElseThrow(() -> ApiException.notFound("Trainer", dto.trainerUsername()));

    Training tr =
        new Training(
            null,
            trainee,
            trainer,
            trainer.getSpecialization(),
            dto.trainingName(),
            dto.date(),
            dto.duration(),
            dto.activeSession());

    trainingRepo.save(tr);
  }

  public Page<TraineeTrainingDto> traineeTrainings(
      String traineeUsername, TrainingFilter f, Pageable pageable) {

    traineeRepo
        .findByUserUsername(traineeUsername)
        .orElseThrow(() -> ApiException.notFound("Trainee", traineeUsername));

    TrainingFilter filter = Optional.ofNullable(f).orElse(TrainingFilter.EMPTY);

    return trainingRepo.findTraineeTrainingRows(
        traineeUsername,
        filter.fromDate(),
        filter.toDate(),
        filter.trainerNameOrNull(),
        filter.trainingTypeOrNull(),
        pageable);
  }

  public List<TrainingDto> listByTrainer(String trainerUsername) {
    return trainingRepo.findByTrainerUserUsername(trainerUsername).stream()
        .map(this::toDto)
        .toList();
  }

  public List<TrainingDto> listByTrainee(String traineeUsername, TrainingFilter filter) {

    var f = Optional.ofNullable(filter).orElse(TrainingFilter.EMPTY);

    return trainingRepo.findTraineeTrainingsJPQL(
        traineeUsername, f.fromDate(), f.toDate(), f.trainerNameOrNull(), f.trainingTypeOrNull());
  }

  public record TrainingFilter(
      LocalDate fromDate,
      LocalDate toDate,
      String trainerName,
      String traineeName,
      String trainingType) {
    /** A pre-built “no criteria” instance so callers can avoid null checks */
    public static final TrainingFilter EMPTY = new TrainingFilter(null, null, null, null, null);

    public String trainerNameOrNull() {
      return blankToNull(trainerName);
    }

    public String traineeNameOrNull() {
      return blankToNull(traineeName);
    }

    public String trainingTypeOrNull() {
      return blankToNull(trainingType);
    }
  }

  private static String blankToNull(String s) {
    return (s == null || s.isBlank()) ? null : s;
  }

  private TrainingDto toDto(Training t) {
    return new TrainingDto(
        t.getId(),
        t.getTrainee().getUser().getUsername(),
        t.getTrainer().getUser().getUsername(),
        t.getTrainer().getUser().getFirstName(),
        t.getTrainer().getUser().getLastName(),
        t.getTrainingType().getName(),
        t.getTrainingName(),
        t.getTrainingDate(),
        t.getTrainingDuration(),
        t.getActiveSession());
  }

  public Page<TrainerTrainingDto> trainerTrainings(
      String trainerUsername, TrainingFilter f, Pageable pageable) {

    trainerRepo
        .findByUserUsername(trainerUsername)
        .orElseThrow(() -> ApiException.notFound("Trainer", trainerUsername));

    TrainingFilter filter = Optional.ofNullable(f).orElse(TrainingFilter.EMPTY);

    return trainingRepo.findTrainerTrainingRows(
        trainerUsername, filter.fromDate(), filter.toDate(), filter.traineeNameOrNull(), pageable);
  }

  @Transactional(readOnly = true)
  public List<TrainingTypeDto> listTrainingTypes() {
    return typeRepo.findAll().stream()
        .map(t -> new TrainingTypeDto(t.getId(), t.getName()))
        .toList();
  }
}

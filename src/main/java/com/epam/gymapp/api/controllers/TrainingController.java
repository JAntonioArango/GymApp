package com.epam.gymapp.api.controllers;

import com.epam.gymapp.api.dto.CreateTrainingDto;
import com.epam.gymapp.api.dto.TraineeTrainingDto;
import com.epam.gymapp.api.dto.TrainerTrainingDto;
import com.epam.gymapp.api.dto.TrainingTypeDto;
import com.epam.gymapp.services.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/training")
@RequiredArgsConstructor
@Tag(name = "Training")
public class TrainingController {

  private final TrainingService service;

  @PostMapping("/add")
  @Operation(summary = "Add Training (14)")
  @PreAuthorize("hasRole('TRAINER') and #body.trainerUsername == authentication.name")
  public ResponseEntity<Void> add(@Valid @RequestBody CreateTrainingDto body) {

    service.addTraining(body);

    return ResponseEntity.ok().build();
  }

  @GetMapping("/trainee/{username}")
  @Operation(summary = "Get Trainee Trainings List (12)")
  @PreAuthorize("(hasRole('TRAINEE') and #username == authentication.name) or hasRole('TRAINER')")
  public ResponseEntity<List<TraineeTrainingDto>> traineeTrainings(
      @PathVariable String username,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) String trainerName,
      @RequestParam(required = false) String trainingType,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    var filter = new TrainingService.TrainingFilter(from, to, trainerName, null, trainingType);

    Page<TraineeTrainingDto> p =
        service.traineeTrainings(username, filter, PageRequest.of(page, size));

    return ResponseEntity.ok(p.getContent());
  }

  @GetMapping("/trainer/{username}")
  @Operation(summary = "Get Trainer Trainings List (13)")
  @PreAuthorize("(hasRole('TRAINER') and #username == authentication.name) or hasRole('TRAINEE')")
  public ResponseEntity<List<TrainerTrainingDto>> trainerTrainings(
      @PathVariable String username,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false) String traineeName,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    var filter = new TrainingService.TrainingFilter(from, to, null, traineeName, null);

    Page<TrainerTrainingDto> p =
        service.trainerTrainings(username, filter, PageRequest.of(page, size));

    return ResponseEntity.ok(p.getContent());
  }

  @GetMapping("/types")
  @Operation(summary = "Get Training Types (17)")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<TrainingTypeDto>> listTypes() {

    List<TrainingTypeDto> types = service.listTrainingTypes();

    return ResponseEntity.ok(types);
  }
}

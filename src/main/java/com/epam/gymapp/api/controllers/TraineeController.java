package com.epam.gymapp.api.controllers;

import com.epam.gymapp.api.dto.CreateTraineeDto;
import com.epam.gymapp.api.dto.TraineeDto;
import com.epam.gymapp.api.dto.TraineeProfileDto;
import com.epam.gymapp.api.dto.TraineeRegistrationDto;
import com.epam.gymapp.api.dto.TrainerShortDto;
import com.epam.gymapp.api.dto.UpdateTraineeDto;
import com.epam.gymapp.api.dto.UpdateTraineeTrainersDto;
import com.epam.gymapp.services.TraineeService;
import com.epam.gymapp.services.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainee")
@RequiredArgsConstructor
@Tag(name = "Trainee")
public class TraineeController {

  private final TraineeService traineeService;
  private final TrainerService trainerService;

  @PostMapping("/register")
  @Operation(summary = "Trainee Registration (1)")
  public ResponseEntity<TraineeRegistrationDto> create(@Valid @RequestBody CreateTraineeDto body) {

    TraineeRegistrationDto created = traineeService.register(body);

    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @GetMapping("/{username}")
  @Operation(summary = "Get trainee profile (5)")
  @PreAuthorize("isAuthenticated()")
  public TraineeProfileDto get(@PathVariable String username) {

    return traineeService.findProfile(username);
  }

  @GetMapping("/{username}/unassigned-trainers")
  @Operation(summary = "Get not assigned on trainee active trainers (10)")
  @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
  public ResponseEntity<List<TrainerShortDto>> unassignedActiveTrainers(
      @PathVariable String username) {

    return ResponseEntity.ok(trainerService.unassignedActiveTrainers(username));
  }

  @GetMapping
  @Operation(summary = "List trainees (paged)")
  @PreAuthorize("hasRole('TRAINER')")
  public ResponseEntity<List<TraineeDto>> list(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

    return ResponseEntity.ok(traineeService.list(PageRequest.of(page, size)).getContent());
  }

  @PutMapping("/{username}")
  @Operation(summary = "Update trainee profile (6)")
  @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
  public TraineeProfileDto updateProfile(
      @PathVariable String username, @Valid @RequestBody UpdateTraineeDto body) {

    return traineeService.updateProfile(username, body);
  }

  @PutMapping("/{username}/trainers")
  @Operation(summary = "Update Trainee's Trainer List (11)")
  @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
  public ResponseEntity<List<TrainerShortDto>> replaceTrainers(
      @PathVariable String username, @Valid @RequestBody UpdateTraineeTrainersDto body) {

    List<TrainerShortDto> out = traineeService.replaceTrainers(username, body.trainers());

    return ResponseEntity.ok(out);
  }

  @DeleteMapping("/{username}")
  @Operation(summary = "Delete trainee profile (7)")
  @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
  public ResponseEntity<Void> delete(@PathVariable String username) {

    traineeService.deleteByUsername(username);
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/{username}/active")
  @Operation(summary = "Activate/De-Activate Trainee (15)")
  @PreAuthorize("hasRole('TRAINEE') and #username == authentication.name")
  public ResponseEntity<Void> setActive(
      @PathVariable String username, @RequestParam boolean active) {

    traineeService.setActive(username, active);
    return ResponseEntity.ok().build();
  }
}

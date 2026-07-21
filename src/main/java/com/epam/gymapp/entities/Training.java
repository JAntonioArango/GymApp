package com.epam.gymapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Training {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "trainee_id")
  private Trainee trainee;

  @ManyToOne(optional = false)
  @JoinColumn(name = "trainer_id")
  private Trainer trainer;

  @ManyToOne(optional = false)
  @JoinColumn(name = "training_type_id")
  private TrainingType trainingType;

  @Column private String trainingName;

  @Column
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate trainingDate;

  @Column private Integer trainingDuration;

  @Column private Boolean activeSession;
}

package com.epam.gymapp.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "specialization_id")
  private TrainingType specialization;

  @OneToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @ManyToMany(mappedBy = "trainers")
  private Set<Trainee> trainees = new HashSet<>();

  @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Training> trainings = new HashSet<>();
}

package com.epam.gymapp.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trainees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trainee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private LocalDate dateOfBirth;

  @Column private String address;

  @OneToOne(optional = false, cascade = CascadeType.ALL)
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @ManyToMany
  @JoinTable(
      name = "trainee_trainer",
      joinColumns = @JoinColumn(name = "trainee_id"),
      inverseJoinColumns = @JoinColumn(name = "trainer_id"))
  private Set<Trainer> trainers = new HashSet<>();

  @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Training> trainings = new HashSet<>();
}

package com.epam.gymapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private String firstName;

  @Column private String lastName;

  @Column(unique = true)
  private String username;

  @Column private String password;

  @Column private boolean isActive = true;

  @Enumerated(EnumType.STRING)
  @Column
  private Role role;

  @OneToOne(mappedBy = "user")
  private Trainer trainer;

  @OneToOne(mappedBy = "user")
  private Trainee trainee;
}

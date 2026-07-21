package com.epam.gymapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

  @Id
  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private int failureCount;

  private Instant lockedUntil;

  @Version private long version;
}

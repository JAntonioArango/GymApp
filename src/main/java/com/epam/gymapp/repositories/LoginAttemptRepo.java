package com.epam.gymapp.repositories;

import com.epam.gymapp.entities.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginAttemptRepo extends JpaRepository<LoginAttempt, String> {}

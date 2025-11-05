package com.epam.gymapp.microserviceTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.microservice.TrainerWorkloadSummary;
import java.util.List;
import org.junit.jupiter.api.Test;

class TrainerWorkloadSummaryTest {

  @Test
  void constructor_allParameters_fieldsSetAndCopyEqual() {
    List<Integer> years = List.of(2024, 2025);
    List<String> months = List.of("January", "February");

    TrainerWorkloadSummary summary =
        new TrainerWorkloadSummary("user", "first", "last", true, years, months, 120);
    TrainerWorkloadSummary copy =
        new TrainerWorkloadSummary(
            summary.getUsername(),
            summary.getFirstName(),
            summary.getLastName(),
            summary.isActive(),
            summary.getYears(),
            summary.getMonths(),
            summary.getTotalDuration());

    assertEquals(summary, copy);
  }

  @Test
  void equals_sameValues_objectsEqualWithSameHashCode() {
    List<Integer> years = List.of(2024);
    List<String> months = List.of("January");
    TrainerWorkloadSummary s1 =
        new TrainerWorkloadSummary("user", "first", "last", true, years, months, 60);
    TrainerWorkloadSummary s2 =
        new TrainerWorkloadSummary("user", "first", "last", true, years, months, 60);

    assertEquals(s1, s2);
    assertEquals(s1.hashCode(), s2.hashCode());
  }
}

package com.epam.gymapp.microserviceTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.microservice.*;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkloadClientFallbackTest {

  private final WorkloadClientFallback fallback = new WorkloadClientFallback();

  @Test
  void save_validWorkload_fallbackResponseReturned() {
    TrainerWorkload input = new TrainerWorkload("user", "first", "last", true, Instant.now(), 60);

    TrainerWorkload result = fallback.save(input);

    assertEquals("user", result.username());
    assertEquals("first", result.firstName());
    assertEquals("last", result.lastName());
    assertTrue(result.active());
    assertEquals(60, result.trainingDuration());
    assertNotNull(result.trainingDate());
  }

  @Test
  void summary_validUsername_fallbackSummaryReturned() {
    TrainerWorkloadSummary result = fallback.summary("user");

    // replace methods with getters

    assertEquals("user", result.getUsername());
    assertEquals("Fallback", result.getFirstName());
    assertEquals("Fallback", result.getLastName());
    assertTrue(result.isActive());
    assertEquals(List.of(2025), result.getYears());
    assertEquals(List.of("January"), result.getMonths());
    assertEquals(0, result.getTotalDuration());
  }

  @Test
  void delete_validId_operationCompleted() {
    assertDoesNotThrow(() -> fallback.delete(1L));
  }
}

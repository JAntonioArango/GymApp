package com.epam.gymapp.microserviceTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.microservice.*;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class WorkloadClientFallbackFactoryTest {

  private final WorkloadClientFallbackFactory factory = new WorkloadClientFallbackFactory();

  @Test
  void create_exceptionCause_workloadClientReturned() {
    RuntimeException cause = new RuntimeException("Service down");

    WorkloadClient client = factory.create(cause);

    assertNotNull(client);
  }

  @Test
  void create_saveOperation_fallbackResponseReturned() {
    WorkloadClient client = factory.create(new RuntimeException("Error"));
    TrainerWorkload input = new TrainerWorkload("user", "first", "last", true, Instant.now(), 60);

    TrainerWorkload result = client.save(input);

    assertEquals("user", result.username());
    assertEquals("first", result.firstName());
    assertEquals("last", result.lastName());
    assertTrue(result.active());
    assertEquals(60, result.trainingDuration());
    assertNotNull(result.trainingDate());
  }

  @Test
  void create_summaryOperation_fallbackSummaryReturned() {
    WorkloadClient client = factory.create(new RuntimeException("Error"));

    TrainerWorkloadSummary result = client.summary("user");

    assertEquals("user", result.getUsername());
    assertEquals("Fallback", result.getFirstName());
    assertEquals("Fallback", result.getLastName());
    assertTrue(result.isActive());
    assertEquals(List.of(2025), result.getYears());
    assertEquals(List.of("January"), result.getMonths());
    assertEquals(0, result.getTotalDuration());
  }

  @Test
  void create_deleteOperation_operationCompleted() {
    WorkloadClient client = factory.create(new RuntimeException("Error"));

    assertDoesNotThrow(() -> client.delete(1L));
  }
}

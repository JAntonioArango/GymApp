package com.epam.gymapp.microserviceTest;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.microservice.TrainerWorkload;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class TrainerWorkloadTest {

  @Test
  void constructor_allParameters_fieldsSetCorrectly() {
    Instant now = Instant.now();

    TrainerWorkload workload = new TrainerWorkload("user", "first", "last", true, now, 60);

    assertEquals("user", workload.username());
    assertEquals("first", workload.firstName());
    assertEquals("last", workload.lastName());
    assertTrue(workload.active());
    assertEquals(now, workload.trainingDate());
    assertEquals(60, workload.trainingDuration());
  }

  @Test
  void equals_sameValues_objectsEqualWithSameHashCode() {
    Instant now = Instant.now();
    TrainerWorkload w1 = new TrainerWorkload("user", "first", "last", true, now, 60);
    TrainerWorkload w2 = new TrainerWorkload("user", "first", "last", true, now, 60);

    assertEquals(w1, w2);
    assertEquals(w1.hashCode(), w2.hashCode());
  }
}

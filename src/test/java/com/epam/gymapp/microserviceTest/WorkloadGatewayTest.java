package com.epam.gymapp.microserviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.microservice.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkloadGatewayTest {

  @Mock private WorkloadClient client;
  @InjectMocks private WorkloadGateway gateway;

  @Test
  void save_validWorkload_clientCalledAndWorkloadReturned() {
    TrainerWorkload workload =
        new TrainerWorkload("user", "first", "last", true, Instant.now(), 60);
    when(client.save(workload)).thenReturn(workload);

    TrainerWorkload result = gateway.save(workload);

    assertEquals(workload, result);
    verify(client).save(workload);
  }

  @Test
  void summary_validUsername_clientCalledAndSummaryReturned() {
    TrainerWorkloadSummary summary =
        new TrainerWorkloadSummary("user", "first", "last", true, null, null, 0);
    when(client.summary("user")).thenReturn(summary);

    TrainerWorkloadSummary result = gateway.summary("user");

    assertEquals(summary, result);
    verify(client).summary("user");
  }

  @Test
  void delete_validId_clientCalled() {
    gateway.delete(1L);
    verify(client).delete(1L);
  }
}

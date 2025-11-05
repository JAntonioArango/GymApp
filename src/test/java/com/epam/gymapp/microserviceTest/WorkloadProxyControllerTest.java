package com.epam.gymapp.microserviceTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.activemq.ProducerController;
import com.epam.gymapp.microservice.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkloadProxyControllerTest {

  @Mock private WorkloadGateway gateway;
  @Mock private ProducerController producerController;
  @InjectMocks private WorkloadProxyController controller;

  @Test
  void save_validWorkload_messageQueuedAndConfirmationReturned() {
    TrainerWorkload workload =
        new TrainerWorkload("user", "first", "last", true, Instant.now(), 60);

    String result = controller.save(workload).getBody();

    assertEquals("Workload queued for processing", result);
    verify(producerController).send("Asynchronous.Task", workload.toString());
  }

  @Test
  void getSummary_validUsername_gatewayCalledAndSummaryReturned() {
    TrainerWorkloadSummary summary =
        new TrainerWorkloadSummary("user", "first", "last", true, null, null, 0);
    when(gateway.summary("user")).thenReturn(summary);

    TrainerWorkloadSummary result = controller.getSummary("user").getBody();

    assertEquals(summary, result);
    verify(gateway).summary("user");
  }

  @Test
  void delete_validId_gatewayCalledForDeletion() {
    controller.delete(1L);
    verify(gateway).delete(1L);
  }
}

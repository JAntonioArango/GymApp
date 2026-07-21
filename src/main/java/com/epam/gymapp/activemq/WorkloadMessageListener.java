package com.epam.gymapp.activemq;

import com.epam.gymapp.microservice.TrainerWorkload;
import com.epam.gymapp.microservice.WorkloadGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkloadMessageListener {

  private final JmsTemplate jmsTemplate;
  private final ObjectMapper objectMapper;
  private final WorkloadGateway workloadGateway;

  @JmsListener(destination = "Asynchronous.Task")
  public void processWorkload(String message) {
    try {
      TrainerWorkload workload = objectMapper.readValue(message, TrainerWorkload.class);

      if (isValidWorkload(workload)) {
        log.info("Processing valid workload for trainer: {}", workload.username());
        workloadGateway.save(workload);

      } else {
        log.warn("Invalid workload detected, sending to DLQ: {}", message);
        jmsTemplate.convertAndSend("DLQ.Asynchronous.Task", message);
      }
    } catch (Exception e) {
      log.error("Failed to parse workload message, sending to DLQ: {}", message, e);
      jmsTemplate.convertAndSend("DLQ.Asynchronous.Task", message);
    }
  }

  private boolean isValidWorkload(TrainerWorkload workload) {
    return isValidString(workload.username())
        && isValidString(workload.firstName())
        && isValidString(workload.lastName())
        && workload.trainingDate() != null
        && workload.trainingDuration() > 0;
  }

  private boolean isValidString(String value) {
    return value != null && !value.trim().isEmpty();
  }
}

package com.epam.gymapp.microservice;

import com.epam.gymapp.activemq.ProducerController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/micro/v1")
@RequiredArgsConstructor
@Slf4j
public class WorkloadProxyController {

  private final WorkloadGateway gateway;
  private final ProducerController producerController;
  private final ObjectMapper objectMapper;

  @PostMapping("/saveworkload")
  public ResponseEntity<String> save(@RequestBody TrainerWorkload workLoad)
      throws JsonProcessingException {
    String queue = "Asynchronous.Task";
    producerController.send(queue, objectMapper.writeValueAsString(workLoad));
    log.info("Workload queued for processing: {}", workLoad.username());
    return ResponseEntity.accepted().body("Workload queued for processing");
  }

  @GetMapping("/summary/{username}")
  public ResponseEntity<TrainerWorkloadSummary> getSummary(
      @PathVariable("username") String username) {
    return ResponseEntity.ok(gateway.summary(username));
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
    gateway.delete(id);
    return ResponseEntity.noContent().build();
  }
}

package com.epam.gymapp.microservice;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
    name = "MICROSERVICE-TASK", // Service name registered in Eureka
    path = "/api/workload/v1",
    fallback = WorkloadClientFallback.class)
public interface WorkloadClient {

  String MICROSERVICE_NAME = "MICROSERVICE-TASK";

  @PostMapping("/saveworkload")
  @CircuitBreaker(name = MICROSERVICE_NAME)
  TrainerWorkload save(@RequestBody TrainerWorkload body);

  @GetMapping("/summary/{username}")
  @CircuitBreaker(name = MICROSERVICE_NAME)
  TrainerWorkloadSummary summary(@PathVariable("username") String username);

  @DeleteMapping("/delete/{id}")
  @CircuitBreaker(name = MICROSERVICE_NAME)
  void delete(@PathVariable("id") Long id);
}

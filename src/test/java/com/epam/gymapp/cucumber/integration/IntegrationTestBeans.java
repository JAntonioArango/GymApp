package com.epam.gymapp.cucumber.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class IntegrationTestBeans {

  @Bean
  public TestRestTemplate testRestTemplate() {
    return new TestRestTemplate();
  }

  @Bean
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  @Bean
  public TestActuatorController testActuatorController() {
    return new TestActuatorController();
  }
}

package com.epam.gymapp.cucumber.integration;

import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class PortAvailableSteps {

  @Autowired private Environment env;

  @Then("the local server port should be available")
  public void theLocalServerPortShouldBeAvailable() {
    String port = env.getProperty("local.server.port");
    if (port == null) {
      throw new AssertionError(
          "local.server.port should be available when Spring Boot test context starts");
    }
  }
}

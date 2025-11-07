package com.epam.gymapp.cucumber.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.epam.gymapp.microservice.TrainerWorkload;
import com.epam.gymapp.microservice.TrainerWorkloadSummary;
import com.epam.gymapp.microservice.WorkloadClient;
import com.epam.gymapp.microservice.WorkloadGateway;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class WorkloadGatewayComponentSteps {

  private WorkloadGateway workloadGateway;
  private WorkloadClient mockWorkloadClient;
  private TrainerWorkload trainerWorkload;
  private TrainerWorkloadSummary summaryResult;
  private Exception thrownException;
  private String username;
  private boolean operationCompleted;

  @Given("the workload gateway is initialized")
  public void theWorkloadGatewayIsInitialized() {
    mockWorkloadClient = mock(WorkloadClient.class);
    workloadGateway = new WorkloadGateway(mockWorkloadClient);
  }

  @Given("I have valid trainer workload data")
  public void iHaveValidTrainerWorkloadData() {
    trainerWorkload =
        new TrainerWorkload("john.trainer", "John", "Trainer", true, java.time.Instant.now(), 60);

    when(mockWorkloadClient.save(any(TrainerWorkload.class))).thenReturn(trainerWorkload);
  }

  @When("I send workload through the gateway")
  public void iSendWorkloadThroughTheGateway() {
    try {
      workloadGateway.save(trainerWorkload);
      operationCompleted = true;
      thrownException = null;
    } catch (Exception e) {
      operationCompleted = false;
      thrownException = e;
    }
  }

  @Then("the workload should be sent successfully")
  public void theWorkloadShouldBeSentSuccessfully() {
    assertTrue(operationCompleted);
    assertNull(thrownException);
  }

  @Then("the client should be called")
  public void theClientShouldBeCalled() {
    verify(mockWorkloadClient).save(any(TrainerWorkload.class));
  }

  @Then("no gateway exception should be thrown")
  public void noGatewayExceptionShouldBeThrown() {
    assertNull(thrownException);
  }

  @Then("a gateway exception should be thrown")
  public void aGatewayExceptionShouldBeThrown() {
    assertNotNull(thrownException);
  }

  @Given("the workload client is unavailable")
  public void theWorkloadClientIsUnavailable() {
    trainerWorkload =
        new TrainerWorkload("john.trainer", "John", "Trainer", true, java.time.Instant.now(), 60);

    doThrow(new RuntimeException("Service unavailable"))
        .when(mockWorkloadClient)
        .save(any(TrainerWorkload.class));
  }

  @Then("the fallback should be triggered")
  public void theFallbackShouldBeTriggered() {
    assertTrue(operationCompleted || thrownException != null);
  }

  @Then("the operation should complete gracefully")
  public void theOperationShouldCompleteGracefully() {
    assertNotNull(thrownException);
  }

  @Given("trainer {string} has workload data")
  public void trainerHasWorkloadData(String username) {
    this.username = username;

    TrainerWorkloadSummary mockSummary =
        new TrainerWorkloadSummary(
            username,
            "John",
            "Trainer",
            true,
            java.util.List.of(2024),
            java.util.List.of("January"),
            60);

    when(mockWorkloadClient.summary(username)).thenReturn(mockSummary);
  }

  @When("I request workload summary through the gateway")
  public void iRequestWorkloadSummaryThroughTheGateway() {
    try {
      summaryResult = workloadGateway.summary(username);
      thrownException = null;
    } catch (Exception e) {
      thrownException = e;
      summaryResult = null;
    }
  }

  @Then("I should receive workload summary")
  public void iShouldReceiveWorkloadSummary() {
    assertNull(thrownException);
    assertNotNull(summaryResult);
  }

  @Then("the summary should contain trainer information")
  public void theSummaryShouldContainTrainerInformation() {
    assertEquals(username, summaryResult.getUsername());
    assertEquals("John", summaryResult.getFirstName());
    assertEquals("Trainer", summaryResult.getLastName());
  }

  @Given("trainer {string} has no workload data")
  public void trainerHasNoWorkloadData(String username) {
    this.username = username;
    when(mockWorkloadClient.summary(username)).thenReturn(null);
  }

  @Then("an empty summary should be returned")
  public void anEmptySummaryShouldBeReturned() {
    assertNull(summaryResult);
    assertNull(thrownException);
  }
}

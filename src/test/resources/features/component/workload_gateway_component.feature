Feature: Workload Gateway Component Tests
  As a developer
  I want to test workload gateway components in isolation
  So that I can ensure microservice communication works correctly

  Background:
    Given the workload gateway is initialized

  Scenario: Send workload data successfully
    Given I have valid trainer workload data
    When I send workload through the gateway
    Then the workload should be sent successfully
    And the client should be called
    And no gateway exception should be thrown

  Scenario: Handle client failure with fallback
    Given the workload client is unavailable
    When I send workload through the gateway
    Then the fallback should be triggered
    And the operation should complete gracefully
    And a gateway exception should be thrown

  Scenario: Get workload summary successfully
    Given trainer "john.trainer" has workload data
    When I request workload summary through the gateway
    Then I should receive workload summary
    And the summary should contain trainer information
    And no gateway exception should be thrown

  Scenario: Handle empty workload summary
    Given trainer "unknown.trainer" has no workload data
    When I request workload summary through the gateway
    Then an empty summary should be returned
    And no gateway exception should be thrown
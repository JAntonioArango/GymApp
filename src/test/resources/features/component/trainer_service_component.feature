Feature: Trainer Service Component Tests
  As a developer
  I want to test trainer service components in isolation
  So that I can ensure business logic works correctly

  Background:
    Given the trainer service is initialized

  Scenario: Create trainer with valid data
    Given I have valid trainer creation data
    When I create trainer through the service
    Then the trainer should be created successfully
    And credentials should be generated
    And the repository should be called

  Scenario: Get trainer profile for existing trainer
    Given trainer "john.trainer" exists in the system
    When I get trainer profile through the service
    Then I should receive trainer profile data
    And the profile should contain correct information
    And no service exception should be thrown

  Scenario: Get trainer profile for non-existing trainer
    Given trainer "unknown.trainer" does not exist
    When I get trainer profile through the service
    Then a not found exception should be thrown
    And no profile data should be returned

  Scenario: Update trainer profile with valid data
    Given trainer "jane.trainer" exists with current data
    When I update trainer profile through the service
    Then the trainer should be updated successfully
    And the repository should save the changes
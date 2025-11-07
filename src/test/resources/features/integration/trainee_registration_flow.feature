Feature: Trainee Registration and Profile Management Flow
  As a gym system user
  I want to register a trainee and manage their profile
  So that I can use the complete trainee lifecycle

  Scenario: Complete trainee registration and profile management
    Given the gym application is running
    When I register a new trainee with name "John" and surname "Doe"
    Then the trainee should be created with generated credentials
    And I should be able to login with the generated credentials
    When I retrieve the trainee profile
    Then the profile should contain correct personal information
    When I update the trainee profile with new address "123 New Street"
    Then the profile should be updated successfully
    When I deactivate the trainee account
    Then the trainee should be marked as inactive
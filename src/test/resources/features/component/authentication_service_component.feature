Feature: Authentication Service Component Tests
  As a developer
  I want to test authentication service components in isolation
  So that I can ensure authentication logic works correctly

  Background:
    Given the authentication service is initialized

  Scenario: Authenticate user with valid credentials
    Given I have valid credentials for user "john.trainer"
    When I authenticate through the service
    Then authentication should succeed
    And a JWT token should be generated
    And no authentication exception should be thrown

  Scenario: Authenticate user with invalid password
    Given I have invalid credentials for user "john.trainer"
    When I authenticate through the service
    Then authentication should fail
    And no JWT token should be generated
    And an authentication exception should be thrown

  Scenario: Authenticate non-existent user
    Given I have credentials for non-existent user "unknown.user"
    When I authenticate through the service
    Then authentication should fail
    And no JWT token should be generated
    And an authentication exception should be thrown

  Scenario: Change password with valid data
    Given I have valid password change data for user "jane.trainer"
    When I change password through the service
    Then password should be updated successfully
    And the new password should be encoded
    And no authentication service exception should be thrown
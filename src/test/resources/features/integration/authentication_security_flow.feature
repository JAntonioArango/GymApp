Feature: Authentication and Security Flow
  As a gym system user
  I want to authenticate securely and manage my session
  So that my account remains protected

  Scenario: Complete authentication lifecycle with security features
    Given the gym application is running
    And a trainee exists with username "test.user" and password "password123"
    When I login with valid credentials
    Then I should receive a JWT token
    And I should be able to access protected endpoints
    When I change my password to "newPassword456"
    Then the password should be updated successfully
    When I logout from the system
    Then my token should be revoked
    And I should not be able to access protected endpoints with the old token

  Scenario: Brute force protection mechanism
    Given the gym application is running
    And a trainee exists with username "test.user2" and password "password123"
    When I attempt to login with wrong password 3 times
    Then the account should be temporarily locked
    And subsequent login attempts should be blocked for 5 minutes
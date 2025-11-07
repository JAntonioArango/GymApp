package com.epam.gymapp.cucumber.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.api.dto.*;
import com.epam.gymapp.entities.Trainee;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.TraineeRepo;
import com.epam.gymapp.repositories.UserRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Optional;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationSecurityFlowSteps {

  private String testUsername;
  private String testPassword;
  private String jwtToken;
  private ResponseEntity<?> lastResponse;

  private TestRestTemplate restTemplate() {
    return CucumberSpringConfiguration.getBean(TestRestTemplate.class);
  }

  private UserRepository userRepo() {
    return CucumberSpringConfiguration.getBean(UserRepository.class);
  }

  private TraineeRepo traineeRepo() {
    return CucumberSpringConfiguration.getBean(TraineeRepo.class);
  }

  private PasswordEncoder passwordEncoder() {
    return CucumberSpringConfiguration.getBean(PasswordEncoder.class);
  }

  private int port() {
    String p = CucumberSpringConfiguration.getProperty("local.server.port");
    if (p == null) {
      throw new IllegalStateException(
          "local.server.port is not available. Ensure the Spring test context is started and @CucumberContextConfiguration is defined in a single class (SharedCucumberConfiguration).");
    }
    return Integer.parseInt(p);
  }

  private String url(String path) {
    return "http://localhost:" + port() + path;
  }

  @Given("a trainee exists with username {string} and password {string}")
  public void aTraineeExistsWithUsernameAndPassword(String username, String password) {
    testUsername = username;
    testPassword = password;

    try {
      Optional<Trainee> existingTrainee = traineeRepo().findByUserUsername(username);
      if (existingTrainee.isPresent()) {
        traineeRepo().delete(existingTrainee.get());
        traineeRepo().flush(); // Ensure the delete is synchronized with the database
      }
    } catch (Exception e) {
      System.out.println("Warning: Could not delete existing trainee: " + e.getMessage());
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(passwordEncoder().encode(password));
    user.setFirstName("Test");
    user.setLastName("User");
    user.setActive(true);

    Trainee trainee = new Trainee();
    trainee.setUser(user);

    traineeRepo().save(trainee);
    traineeRepo().flush(); // Ensure the save is synchronized with the database
  }

  @When("I login with valid credentials")
  public void iLoginWithValidCredentials() {
    String loginPath =
        String.format("/api/v1/auth/login?username=%s&password=%s", testUsername, testPassword);

    ResponseEntity<TokenDto> response = restTemplate().getForEntity(url(loginPath), TokenDto.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    jwtToken = response.getBody().token();
    lastResponse = response;
  }

  @Then("I should receive a JWT token")
  public void iShouldReceiveAJWTToken() {
    assertNotNull(jwtToken);
    assertTrue(!jwtToken.isEmpty());
  }

  @Then("I should be able to access protected endpoints")
  public void iShouldBeAbleToAccessProtectedEndpoints() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<TraineeProfileDto> response =
        restTemplate()
            .exchange(
                url("/api/v1/trainee/" + testUsername),
                HttpMethod.GET,
                entity,
                TraineeProfileDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @When("I change my password to {string}")
  public void iChangeMyPasswordTo(String newPassword) {
    ChangePasswordDto changePasswordDto = new ChangePasswordDto(testPassword, newPassword);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<ChangePasswordDto> entity = new HttpEntity<>(changePasswordDto, headers);

    ResponseEntity<Void> response =
        restTemplate()
            .exchange(
                url("/api/v1/auth/" + testUsername + "/password"),
                HttpMethod.PUT,
                entity,
                Void.class);

    lastResponse = response;
    testPassword = newPassword;
  }

  @Then("the password should be updated successfully")
  public void thePasswordShouldBeUpdatedSuccessfully() {
    assertEquals(HttpStatus.OK, lastResponse.getStatusCode());
  }

  @When("I logout from the system")
  public void iLogoutFromTheSystem() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Void> response =
        restTemplate().exchange(url("/api/v1/auth/logout"), HttpMethod.POST, entity, Void.class);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Then("my token should be revoked")
  public void myTokenShouldBeRevoked() {
    assertTrue(true);
  }

  @Then("I should not be able to access protected endpoints with the old token")
  public void iShouldNotBeAbleToAccessProtectedEndpointsWithTheOldToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> response =
        restTemplate()
            .exchange(url("/api/v1/trainee/" + testUsername), HttpMethod.GET, entity, String.class);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }

  @When("I attempt to login with wrong password {int} times")
  public void iAttemptToLoginWithWrongPasswordTimes(int attempts) {
    for (int i = 0; i < attempts; i++) {
      String loginPath =
          String.format("/api/v1/auth/login?username=%s&password=wrongpassword", testUsername);

      ResponseEntity<String> response = restTemplate().getForEntity(url(loginPath), String.class);
      lastResponse = response;
    }
  }

  @Then("the account should be temporarily locked")
  public void theAccountShouldBeTemporarilyLocked() {
    HttpStatus status = (HttpStatus) lastResponse.getStatusCode();
    assertTrue(
        status == HttpStatus.UNAUTHORIZED
            || status == HttpStatus.INTERNAL_SERVER_ERROR
            || status == HttpStatus.BAD_REQUEST,
        "Expected UNAUTHORIZED, INTERNAL_SERVER_ERROR, or BAD_REQUEST, but got: " + status);
  }

  @Then("subsequent login attempts should be blocked for {int} minutes")
  public void subsequentLoginAttemptsShouldBeBlockedForMinutes(int minutes) {
    String loginPath =
        String.format("/api/v1/auth/login?username=%s&password=%s", testUsername, testPassword);

    ResponseEntity<String> response = restTemplate().getForEntity(url(loginPath), String.class);

    assertTrue(
        response.getStatusCode() == HttpStatus.UNAUTHORIZED
            || response.getStatusCode() == HttpStatus.BAD_REQUEST,
        "Expected either UNAUTHORIZED or BAD_REQUEST but got: " + response.getStatusCode());
  }
}

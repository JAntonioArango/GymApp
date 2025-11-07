package com.epam.gymapp.cucumber.integration;

import static org.junit.jupiter.api.Assertions.*;

import com.epam.gymapp.api.dto.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

public class TraineeRegistrationFlowSteps {

  private TraineeRegistrationDto registrationResponse;
  private String jwtToken;
  private TraineeProfileDto profileResponse;

  private String registeredFirstName;
  private String registeredLastName;

  private TestRestTemplate restTemplate() {
    return CucumberSpringConfiguration.getBean(TestRestTemplate.class);
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

  @Given("the gym application is running")
  public void theGymApplicationIsRunning() {
    ResponseEntity<String> response =
        restTemplate().getForEntity(url("/ops/gym-health"), String.class);
    if (!response.getStatusCode().equals(HttpStatus.OK)) {
      String body = response.getBody();
      fail(
          "Health endpoint returned "
              + response.getStatusCode()
              + ". Body: "
              + (body == null ? "<empty>" : body));
    }
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @When("I register a new trainee with name {string} and surname {string}")
  public void iRegisterANewTraineeWithNameAndSurname(String firstName, String lastName) {
    CreateTraineeDto createDto = new CreateTraineeDto(firstName, lastName, null, null);

    ResponseEntity<TraineeRegistrationDto> response =
        restTemplate()
            .postForEntity(
                url("/api/v1/trainee/register"), createDto, TraineeRegistrationDto.class);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    registrationResponse = response.getBody();
    assertNotNull(registrationResponse, "Registration response must not be null");

    this.registeredFirstName = firstName;
    this.registeredLastName = lastName;
  }

  @Then("the trainee should be created with generated credentials")
  public void theTraineeShouldBeCreatedWithGeneratedCredentials() {
    assertNotNull(registrationResponse, "Registration response should not be null");
    assertNotNull(registrationResponse.username(), "Username should not be null");
    assertNotNull(registrationResponse.password(), "Password should not be null");

    String username = registrationResponse.username().toLowerCase();
    assertTrue(
        username.contains("john") && username.contains("doe"),
        "Username should contain both first name and last name. Got: "
            + registrationResponse.username());
  }

  @Then("I should be able to login with the generated credentials")
  public void iShouldBeAbleToLoginWithTheGeneratedCredentials() {
    assertNotNull(registrationResponse, "registrationResponse is required to login");
    String loginUrl =
        String.format(
            "/api/v1/auth/login?username=%s&password=%s",
            registrationResponse.username(), registrationResponse.password());

    ResponseEntity<TokenDto> response = restTemplate().getForEntity(url(loginUrl), TokenDto.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody(), "Login response body is null");
    jwtToken = response.getBody().token();
    assertNotNull(jwtToken);
  }

  @When("I retrieve the trainee profile")
  public void iRetrieveTheTraineeProfile() {
    assertNotNull(jwtToken, "JWT token must be available");
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<TraineeProfileDto> response =
        restTemplate()
            .exchange(
                url("/api/v1/trainee/" + registrationResponse.username()),
                HttpMethod.GET,
                entity,
                TraineeProfileDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    profileResponse = response.getBody();
    assertNotNull(profileResponse, "Profile response must not be null");
  }

  @Then("the profile should contain correct personal information")
  public void theProfileShouldContainCorrectPersonalInformation() {
    assertNotNull(profileResponse);
    assertEquals(
        registeredFirstName != null ? registeredFirstName : "John", profileResponse.firstName());
    assertEquals(
        registeredLastName != null ? registeredLastName : "Doe", profileResponse.lastName());
    assertTrue(profileResponse.isActive());
  }

  @When("I update the trainee profile with new address {string}")
  public void iUpdateTheTraineeProfileWithNewAddress(String address) {
    UpdateTraineeDto updateDto =
        new UpdateTraineeDto(
            registrationResponse.username(),
            registeredFirstName != null ? registeredFirstName : "John",
            registeredLastName != null ? registeredLastName : "Doe",
            null,
            address,
            true);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<UpdateTraineeDto> entity = new HttpEntity<>(updateDto, headers);

    ResponseEntity<TraineeProfileDto> response =
        restTemplate()
            .exchange(
                url("/api/v1/trainee/" + registrationResponse.username()),
                HttpMethod.PUT,
                entity,
                TraineeProfileDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    profileResponse = response.getBody();
    assertNotNull(profileResponse, "Updated profile response must not be null");
  }

  @Then("the profile should be updated successfully")
  public void theProfileShouldBeUpdatedSuccessfully() {
    assertNotNull(profileResponse);
    assertEquals("123 New Street", profileResponse.address());
  }

  @When("I deactivate the trainee account")
  public void iDeactivateTheTraineeAccount() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Void> response =
        restTemplate()
            .exchange(
                url("/api/v1/trainee/" + registrationResponse.username() + "/active?active=false"),
                HttpMethod.PATCH,
                entity,
                Void.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Then("the trainee should be marked as inactive")
  public void theTraineeShouldBeMarkedAsInactive() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<TraineeProfileDto> response =
        restTemplate()
            .exchange(
                url("/api/v1/trainee/" + registrationResponse.username()),
                HttpMethod.GET,
                entity,
                TraineeProfileDto.class);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody(), "Profile response must not be null");
    assertFalse(response.getBody().isActive());
  }
}

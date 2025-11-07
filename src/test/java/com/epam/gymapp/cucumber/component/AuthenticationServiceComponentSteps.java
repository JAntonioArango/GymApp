package com.epam.gymapp.cucumber.component;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.dto.ChangePasswordDto;
import com.epam.gymapp.api.dto.TokenDto;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.UserRepository;
import com.epam.gymapp.services.AuthenticationService;
import com.epam.gymapp.services.JwtService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationServiceComponentSteps {

  private AuthenticationService authenticationService;
  private UserRepository mockUserRepository;
  private JwtService mockJwtService;
  private PasswordEncoder mockPasswordEncoder;
  private String username;
  private String password;
  private TokenDto result;
  private Exception thrownException;
  private ChangePasswordDto changePasswordDto;
  private User validatedUser;

  @Given("the authentication service is initialized")
  public void theAuthenticationServiceIsInitialized() {
    mockUserRepository = mock(UserRepository.class);
    mockJwtService = mock(JwtService.class);
    mockPasswordEncoder = mock(PasswordEncoder.class);
    var mockAuthManager =
        mock(org.springframework.security.authentication.AuthenticationManager.class);
    var mockAttemptService = mock(com.epam.gymapp.services.LoginAttemptService.class);
    authenticationService =
        new AuthenticationService(
            mockAuthManager, mockPasswordEncoder, mockUserRepository, mockAttemptService);
  }

  @Given("I have valid credentials for user {string}")
  public void iHaveValidCredentialsForUser(String username) {
    this.username = username;
    this.password = "validPassword";

    User mockUser = new User();
    mockUser.setUsername(username);
    mockUser.setPassword("encodedPassword");
    mockUser.setActive(true);

    when(mockUserRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
    when(mockJwtService.createToken(username)).thenReturn("jwt-token");
  }

  @Given("I have invalid credentials for user {string}")
  public void iHaveInvalidCredentialsForUser(String username) {
    this.username = username;
    this.password = "invalidPassword";
  }

  @Given("I have credentials for non-existent user {string}")
  public void iHaveCredentialsForNonExistentUser(String username) {
    this.username = username;
    this.password = "anyPassword";
    when(mockUserRepository.findByUsername(username)).thenReturn(java.util.Optional.empty());
  }

  @When("I authenticate through the service")
  public void iAuthenticateThroughTheService() {
    try {
      validatedUser = authenticationService.validate(username, password);
      String token = mockJwtService.createToken(username);
      result = new TokenDto(token);
      thrownException = null;
    } catch (Exception e) {
      thrownException = e;
      result = null;
      validatedUser = null;
    }
  }

  @Then("authentication should succeed")
  public void authenticationShouldSucceed() {
    assertNull(thrownException);
    assertNotNull(validatedUser);
  }

  @Then("a JWT token should be generated")
  public void aJwtTokenShouldBeGenerated() {
    assertNotNull(result);
    assertNotNull(result.token());
    assertEquals("jwt-token", result.token());
  }

  @Then("no authentication exception should be thrown")
  public void noAuthenticationExceptionShouldBeThrown() {
    assertNull(thrownException);
  }

  @Then("authentication should fail")
  public void authenticationShouldFail() {
    assertNotNull(thrownException);
    assertNull(result);
  }

  @Then("no JWT token should be generated")
  public void noJwtTokenShouldBeGenerated() {
    assertNull(result);
  }

  @Then("an authentication exception should be thrown")
  public void anAuthenticationExceptionShouldBeThrown() {
    assertNotNull(thrownException);
  }

  @Given("I have valid password change data for user {string}")
  public void iHaveValidPasswordChangeDataForUser(String username) {
    this.username = username;
    changePasswordDto = new ChangePasswordDto("oldPassword", "newPassword");

    User mockUser = new User();
    mockUser.setUsername(username);
    mockUser.setPassword("encodedOldPassword");

    when(mockUserRepository.findByUsername(username)).thenReturn(java.util.Optional.of(mockUser));
    when(mockPasswordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
  }

  @When("I change password through the service")
  public void iChangePasswordThroughTheService() {
    try {
      authenticationService.validate(username, changePasswordDto.oldPassword());
      authenticationService.changePassword(username, changePasswordDto.newPassword());
      thrownException = null;
    } catch (Exception e) {
      thrownException = e;
    }
  }

  @Then("password should be updated successfully")
  public void passwordShouldBeUpdatedSuccessfully() {
    assertNull(thrownException);
    verify(mockPasswordEncoder).encode("newPassword");
  }

  @Then("the new password should be encoded")
  public void theNewPasswordShouldBeEncoded() {
    verify(mockPasswordEncoder).encode("newPassword");
  }

  @Then("no authentication service exception should be thrown")
  public void noAuthenticationServiceExceptionShouldBeThrown() {
    assertNull(thrownException);
  }
}

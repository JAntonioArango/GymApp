package com.epam.gymapp.api.controllers;

import com.epam.gymapp.api.dto.ChangePasswordDto;
import com.epam.gymapp.api.dto.TokenDto;
import com.epam.gymapp.entities.RevokedToken;
import com.epam.gymapp.entities.User;
import com.epam.gymapp.repositories.RevokedTokenRepo;
import com.epam.gymapp.services.AuthenticationService;
import com.epam.gymapp.services.JwtService;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

  private final AuthenticationService authService;
  private final JwtService jwtService;
  private final RevokedTokenRepo repo;

  @Timed(value = "login.duration", description = "Time spent on login", histogram = true)
  @GetMapping("/login")
  @Operation(summary = "Login (3)")
  public ResponseEntity<TokenDto> login(
      @RequestParam String username, @RequestParam String password) {

    User user = authService.validate(username, password);

    String token = jwtService.createToken(username, user.getRole());

    return ResponseEntity.ok(new TokenDto(token));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    String hdr = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = hdr.substring(7);
    var jwt = jwtService.parse(token);

    repo.save(new RevokedToken(token, jwt.getExpiresAt()));

    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{username}/password")
  @Operation(summary = "Change Password (4)")
  @PreAuthorize("#username == authentication.name")
  public ResponseEntity<Void> changePassword(
      @PathVariable String username, @RequestBody ChangePasswordDto body) {

    authService.validate(username, body.oldPassword());

    authService.changePassword(username, body.newPassword());
    return ResponseEntity.ok().build();
  }
}

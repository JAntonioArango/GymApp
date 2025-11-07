package com.epam.gymapp.cucumber.integration;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test-only controller that exposes the same path as the actuator health mapping (/ops/gym-health)
 * and returns a simple UP response so Cucumber tests don't fail on external health contributors in
 * the test environment.
 */
@RestController
@RequestMapping("/ops")
public class TestActuatorController {

  @GetMapping("/gym-health")
  public ResponseEntity<Map<String, Object>> gymHealth() {
    return ResponseEntity.ok(
        Map.of(
            "status", "UP",
            "components", Map.of(),
            "details", Map.of()));
  }
}

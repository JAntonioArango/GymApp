package com.epam.gymapp.apiTest.controllersTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.epam.gymapp.api.controllers.TrainerController;
import com.epam.gymapp.api.dto.CreateTrainerDto;
import com.epam.gymapp.api.dto.TrainerRegistrationDto;
import com.epam.gymapp.entities.Specialization;
import com.epam.gymapp.services.TrainerService;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TrainerTimeTest {

  @Mock private TrainerService trainerService;

  private MeterRegistry meterRegistry;
  private TimedAspect timedAspect;
  private TrainerController rawController;
  private TrainerController controllerProxy;

  @BeforeEach
  void setUp() {
    meterRegistry = new SimpleMeterRegistry();

    timedAspect = new TimedAspect(meterRegistry);

    rawController = new TrainerController(trainerService);

    AspectJProxyFactory factory = new AspectJProxyFactory(rawController);
    factory.addAspect(timedAspect);

    controllerProxy = (TrainerController) factory.getProxy();
  }

  @Test
  void create_validTrainerDto_timerRecordedAndCreatedStatusReturned() {
    CreateTrainerDto requestDto = new CreateTrainerDto("Alice", "Smith", Specialization.CARDIO);

    TrainerRegistrationDto fakeResponse =
        new TrainerRegistrationDto("alice.smith-1234", "securePass");

    when(trainerService.register(requestDto)).thenReturn(fakeResponse);

    ResponseEntity<TrainerRegistrationDto> response = controllerProxy.create(requestDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(fakeResponse, response.getBody());

    verify(trainerService).register(requestDto);

    Timer timer = meterRegistry.find("create.duration").timer();
    assertNotNull(timer, "Expected a Timer named 'create.duration' to exist");
    assertEquals(1, timer.count(), "Timer should have been recorded exactly once");
  }
}

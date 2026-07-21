package com.epam.gymapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI gymApiInfo() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Gym Task REST API - Juan Antonio Arango")
                .description(
                    """
                                      CRUD operations for Trainers, Trainees and Trainings.
                                      Provides login/password management and rich filtering \
                                      for both trainees and trainers.
                                      """)
                .version("1.4.0")
                .license(new License().name("Apache 2.0")))
        .addServersItem(new Server().url("http://localhost:8080"));
  }

  @Bean
  public GroupedOpenApi gymApi() {
    return GroupedOpenApi.builder()
        .group("gym")
        .packagesToScan("com.epam.gymapp.api.controllers")
        .pathsToMatch("/**")
        .build();
  }
}

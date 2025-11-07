package com.epam.gymapp.cucumber.integration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ComponentScan(
    basePackages = "com.epam.gymapp",
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.epam.gymapp.activemq.JmsConfig.class))
@Import({IntegrationTestBeans.class, CucumberTestConfig.class})
@TestPropertySource(
    properties = {
      "spring.profiles.active=integration-test",
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration,org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration",
      "spring.jms.enabled=false",
      "spring.main.allow-bean-definition-overriding=true",
      "spring.datasource.url=jdbc:h2:mem:integrationtestdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
      "spring.datasource.driver-class-name=org.h2.Driver",
      "spring.datasource.username=sa",
      "spring.datasource.password=",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.jpa.show-sql=false",
      "eureka.client.enabled=false",
      "jwt.secret=${JWT_SECRET_TEST}",
      "jwt.expiration=30"
    })
public class IntegrationTestConfiguration {}

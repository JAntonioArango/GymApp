package com.epam.gymapp.cucumber.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
@ComponentScan(
    basePackages = "com.epam.gymapp",
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = com.epam.gymapp.activemq.JmsConfig.class))
@Import({
  com.epam.gymapp.cucumber.integration.IntegrationTestBeans.class,
  com.epam.gymapp.cucumber.integration.CucumberTestConfig.class
})
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
      "jwt.secret=dGVzdC1zZWNyZXQta2V5LWZvci10ZXN0aW5nLXB1cnBvc2VzLW9ubHk=",
      "jwt.expiration=30",
      // disable problematic health contributors in tests to avoid 503 from actuator
      "management.health.jms.enabled=false",
      "management.health.discovery.enabled=false"
    })
public class SharedCucumberConfiguration implements ApplicationContextAware {

  private static ApplicationContext context;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    context = applicationContext;
  }

  public static <T> T getBean(Class<T> clazz) {
    if (context == null) {
      throw new IllegalStateException("ApplicationContext is not initialized yet");
    }
    return context.getBean(clazz);
  }

  public static String getProperty(String key) {
    if (context != null) {
      return context.getEnvironment().getProperty(key);
    }
    // fallback to system properties or environment variables (convert dots to underscores)
    String sys = System.getProperty(key);
    if (sys != null) return sys;
    String envKey = key.replace('.', '_').toUpperCase();
    return System.getenv(envKey);
  }
}

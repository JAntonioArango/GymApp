package com.epam.gymapp.cucumber.integration;

import com.epam.gymapp.cucumber.config.SharedCucumberConfiguration;

/**
 * Lightweight delegator to the shared cucumber configuration. This class no longer defines the
 * Spring test context to avoid duplicate @CucumberContextConfiguration annotations. Step classes
 * may still call its helpers for convenience.
 */
public class CucumberSpringConfiguration {

  public static <T> T getBean(Class<T> clazz) {
    return SharedCucumberConfiguration.getBean(clazz);
  }

  public static String getProperty(String key) {
    return SharedCucumberConfiguration.getProperty(key);
  }
}

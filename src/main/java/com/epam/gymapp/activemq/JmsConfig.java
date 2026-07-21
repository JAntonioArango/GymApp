package com.epam.gymapp.activemq;

import jakarta.jms.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

@Slf4j
@Configuration
@EnableJms
public class JmsConfig {

  @Bean
  public JmsListenerContainerFactory<?> jmsListenerContainerFactory(
      ConnectionFactory connectionFactory) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setConcurrency("1-1");
    factory.setErrorHandler(
        t -> log.error("An error has occurred in the transaction: {}", t.getMessage(), t));
    return factory;
  }
}

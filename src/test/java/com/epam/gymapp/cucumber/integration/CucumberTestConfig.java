package com.epam.gymapp.cucumber.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.epam.gymapp.activemq.ProducerController;
import com.epam.gymapp.activemq.WorkloadMessageListener;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jms.config.JmsListenerContainerFactory;

@TestConfiguration
public class CucumberTestConfig {

  @MockBean private ProducerController producerController;

  @MockBean private WorkloadMessageListener workloadMessageListener;

  @Bean
  @Primary
  public ConnectionFactory connectionFactory() {
    ConnectionFactory cf = mock(ConnectionFactory.class);
    try {
      Connection conn = mock(Connection.class);
      when(cf.createConnection()).thenReturn(conn);
      when(cf.createConnection(anyString(), anyString())).thenReturn(conn);
    } catch (JMSException e) {
      throw new RuntimeException(e);
    }
    return cf;
  }

  @Bean
  @Primary
  public JmsListenerContainerFactory<?> jmsListenerContainerFactory() {
    return mock(JmsListenerContainerFactory.class);
  }
}

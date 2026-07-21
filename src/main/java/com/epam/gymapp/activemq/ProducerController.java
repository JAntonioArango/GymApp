package com.epam.gymapp.activemq;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/messages")
public class ProducerController {
  private final JmsTemplate jmsTemplate;

  public ProducerController(JmsTemplate jmsTemplate) {
    this.jmsTemplate = jmsTemplate;
  }

  @PostMapping("/{queue}")
  public void send(@PathVariable String queue, @RequestBody String body) {
    jmsTemplate.convertAndSend(queue, body);
  }
}

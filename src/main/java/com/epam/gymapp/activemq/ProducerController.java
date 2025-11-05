package com.epam.gymapp.activemq;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

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

package com.example.assignment.services.kafka;

import com.example.assignment.models.EmailMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EmailProducer {

  @Autowired
  private KafkaTemplate<String, EmailMessage> kafkaTemplate;

  public void sendEmailMessage(EmailMessage emailMessage) {
    kafkaTemplate.send("email-topic", emailMessage);
  }
}
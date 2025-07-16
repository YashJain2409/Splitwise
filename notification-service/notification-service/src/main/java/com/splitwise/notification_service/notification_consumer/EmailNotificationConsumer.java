package com.splitwise.notification_service.notification_consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.splitwise.dto.EmailNotification;


@Service
public class EmailNotificationConsumer {
	
	@KafkaListener(topics = "email-topic",groupId = "splitwise-group")
	public void consue(EmailNotification emailNotification) {
		System.out.println("info logs");
		System.out.println("sending email : " + emailNotification.getTo());
		System.out.println("sending email  : " + emailNotification.getBody());
		System.out.println("sending email : " + emailNotification.getSubject());
		
		//TODO: integration with JavaMailSender.
	}
}

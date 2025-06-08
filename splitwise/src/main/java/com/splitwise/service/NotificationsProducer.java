package com.splitwise.service;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.splitwise.dto.EmailNotification;


@Component
public class NotificationsProducer {
	
	@Autowired
	private KafkaTemplate<String, EmailNotification> kafkaTemplate;
	
	private static final String Topic = "email-topic";
	
	public void sendEmailNotification(EmailNotification email) {
		System.out.println("sending kafka event...");
		kafkaTemplate.send(Topic, email);
	}

}

package com.splitwise.service;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.splitwise.dto.EmailNotification;
import com.splitwise.events.NotificationEvent;


@Component
public class NotificationsProducer {
	
	@Autowired
	private KafkaTemplate<String, NotificationEvent> kafkaTemplate;
	
	private static final String Topic = "notification-events";
	
	public void sendEvent(NotificationEvent event) {
//		System.out.println("sending kafka event...");
		kafkaTemplate.send(Topic, event);
	}

}

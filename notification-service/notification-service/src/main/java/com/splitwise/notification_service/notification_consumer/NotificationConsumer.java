package com.splitwise.notification_service.notification_consumer;

import java.io.IOException;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.splitwise.dto.EmailNotification;
import com.splitwise.events.NotificationEvent;
import com.splitwise.service.NotificationService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class NotificationConsumer {
	
	final NotificationService notificationService;
	
	@KafkaListener(topics = "notification-events",groupId = "splitwise-group")
	public void consume(NotificationEvent notificationEvent)  {	
		notificationService.notifyUser(notificationEvent);
		
	}
}

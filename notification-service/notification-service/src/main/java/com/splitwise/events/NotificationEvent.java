package com.splitwise.events;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class NotificationEvent {
	
	public NotificationEvent() {}
	private NotificationEventType eventType;
	private User recipient;
	
	
}

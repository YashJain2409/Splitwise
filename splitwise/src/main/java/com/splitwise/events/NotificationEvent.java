package com.splitwise.events;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class NotificationEvent {
	private final NotificationEventType eventType;
	private final User recipient;
	
	
}

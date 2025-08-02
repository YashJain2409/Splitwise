package com.splitwise.service;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.events.NotificationEvent;
import com.splitwise.intfc.NotificationObserver;
import com.splitwise.model.NotificationPreferences;
import com.splitwise.model.User;
import com.splitwise.repository.NotificationPreferenceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
	final List<NotificationObserver> observers;
	final NotificationPreferenceRepository notificationPreferenceRepository;
	

	public void notifyUser(NotificationEvent notificationEvent)  {
		User user = notificationEvent.getRecipient();
		NotificationEventType eventType = notificationEvent.getEventType();
		NotificationPreferences pref = notificationPreferenceRepository.findByUserAndNotificationEventType(user, eventType);
		for (NotificationObserver obs : observers) {
			boolean shouldNotify = switch (obs.getChannel()) {
			case EMAIL -> pref.isEmailEnabled();
			case SMS -> pref.isEmailEnabled();
			};
			if (shouldNotify) {
				obs.notify(notificationEvent);
			}
		}
	}
}

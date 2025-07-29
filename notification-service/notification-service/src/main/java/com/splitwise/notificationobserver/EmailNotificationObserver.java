package com.splitwise.notificationobserver;

import org.springframework.stereotype.Component;

import com.splitwise.enums.NotificationChannel;
import com.splitwise.intfc.NotificationObserver;
import com.splitwise.model.User;

@Component
public class EmailNotificationObserver implements NotificationObserver {

	@Override
	public void notify(User user, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NotificationChannel getChannel() {
		return NotificationChannel.EMAIL;
	}

}

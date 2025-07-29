package com.splitwise.intfc;

import com.splitwise.enums.NotificationChannel;
import com.splitwise.model.User;

public interface NotificationObserver {
	 void notify(User user, String message);
	  NotificationChannel getChannel();
}

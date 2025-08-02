package com.splitwise.intfc;

import com.splitwise.enums.NotificationChannel;
import com.splitwise.events.NotificationEvent;
import com.splitwise.model.User;

public interface NotificationObserver  {
	 void notify(NotificationEvent notificationEvent) ; 
	  NotificationChannel getChannel();
}

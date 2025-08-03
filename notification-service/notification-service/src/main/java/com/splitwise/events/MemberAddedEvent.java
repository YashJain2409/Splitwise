package com.splitwise.events;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.Data;

@Data
public class MemberAddedEvent extends NotificationEvent {
	String groupName;
	String addedUserName;
	String addedByUserName;
	String addedByUserEmail;

	public MemberAddedEvent(String groupName, String addedUserName, String addedByUserName, String addedByUserEmail,User userDetails) {
		super(NotificationEventType.USER_ADDED_TO_GROUP,userDetails);
		this.groupName = groupName;
		this.addedUserName = addedUserName;
		this.addedByUserName = addedByUserName;
		this.addedByUserEmail = addedByUserEmail;
	}
	
	
}

package com.splitwise.events;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.Data;

@Data
public class MemberRemovedEvent extends NotificationEvent {
	String groupName;
	String removedByUserName;
	String removedByUserEmail;

	public MemberRemovedEvent(String groupName, String removedByUserName, String removedByUserEmail, User userDetails) {
		super(NotificationEventType.USER_REMOVED_FROM_GROUP, userDetails);
		this.groupName = groupName;
		this.removedByUserName = removedByUserName;
		this.removedByUserEmail = removedByUserEmail;
	}

}

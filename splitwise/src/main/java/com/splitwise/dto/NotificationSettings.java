package com.splitwise.dto;

import lombok.Data;

@Data
public class NotificationSettings {
	private boolean expenseCreated;
	private boolean expenseDeleted;
	private boolean expenseUpdated;
	private boolean memberAdded;
	private boolean memberRemoved;
}

package com.splitwise.events;

import java.math.BigDecimal;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.Data;

@Data
public class ExpenseDeletedEvent extends NotificationEvent {

	private final String expenseName;
	private final BigDecimal expenseAmount;
	private final User userDetails;
	private final BigDecimal owedAmount;
	private final String deletedUserName;

	public ExpenseDeletedEvent(String expenseName, BigDecimal expenseAmount, BigDecimal owedAmount, User userDetails,
			String deletedUserName) {
		super(NotificationEventType.EXPENSE_UPDATED, userDetails);
		this.expenseAmount = expenseAmount;
		this.expenseName = expenseName;
		this.userDetails = userDetails;
		this.owedAmount = owedAmount;
		this.deletedUserName = deletedUserName;
	}

}

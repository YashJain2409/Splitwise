package com.splitwise.events;

import java.math.BigDecimal;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.Data;

@Data
public class ExpenseUpdatedEvent extends NotificationEvent {

	private  String expenseName;
	private  BigDecimal expenseAmount;
	private  User userDetails;
	private  BigDecimal owedAmount;
	private  String createdUserName;

	public ExpenseUpdatedEvent() {
		super();
	}

	public ExpenseUpdatedEvent(String expenseName, BigDecimal expenseAmount, BigDecimal owedAmount, User userDetails,
			String createdUserName) {
		super(NotificationEventType.EXPENSE_UPDATED, userDetails);
		this.expenseAmount = expenseAmount;
		this.expenseName = expenseName;
		this.userDetails = userDetails;
		this.owedAmount = owedAmount;
		this.createdUserName = createdUserName;
	}

}

package com.splitwise.events;

import java.math.BigDecimal;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.Data;

@Data
public class ExpenseDeletedEvent extends NotificationEvent {

	private String expenseName;
	private BigDecimal expenseAmount;
	private User userDetails;
	private BigDecimal owedAmount;
	private String deletedUserName;

	public ExpenseDeletedEvent() {
		super();
	}

	public ExpenseDeletedEvent(String expenseName, BigDecimal expenseAmount, BigDecimal owedAmount, User userDetails,
			String deletedUserName) {
		super(NotificationEventType.EXPENSE_DELETED, userDetails);
		this.expenseAmount = expenseAmount;
		this.expenseName = expenseName;
		this.userDetails = userDetails;
		this.owedAmount = owedAmount;
		this.deletedUserName = deletedUserName;
	}

}

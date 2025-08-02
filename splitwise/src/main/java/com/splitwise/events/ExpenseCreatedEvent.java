package com.splitwise.events;

import java.math.BigDecimal;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.Expense;
import com.splitwise.model.User;

import lombok.Data;


@Data
public class ExpenseCreatedEvent extends NotificationEvent {
	
	private final String expenseName;
	private final BigDecimal expenseAmount;
	private final User userDetails;
	private final BigDecimal owedAmount;
	private final String createdUserName;
	
	public ExpenseCreatedEvent(String expenseName,BigDecimal expenseAmount,BigDecimal owedAmount,User userDetails,String createdUserName) {
		super(NotificationEventType.EXPENSE_CREATED,userDetails);
		this.expenseAmount = expenseAmount;
		this.expenseName = expenseName;
		this.userDetails = userDetails;
		this.owedAmount = owedAmount;
		this.createdUserName = createdUserName;
	}
	
	

}

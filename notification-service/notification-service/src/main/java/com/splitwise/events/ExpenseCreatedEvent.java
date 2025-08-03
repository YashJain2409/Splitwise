package com.splitwise.events;

import java.math.BigDecimal;

import com.splitwise.enums.NotificationEventType;
import com.splitwise.model.User;

import lombok.Data;


@Data
public class ExpenseCreatedEvent extends NotificationEvent {
	
	
	public ExpenseCreatedEvent() {
		super();
	};
	
	private  String expenseName;
	private BigDecimal expenseAmount;
	private  BigDecimal owedAmount;
	private  String createdUserName;
	
	public ExpenseCreatedEvent(String expenseName,BigDecimal expenseAmount,BigDecimal owedAmount,User userDetails,String createdUserName) {
		super(NotificationEventType.EXPENSE_CREATED,userDetails);
		this.expenseAmount = expenseAmount;
		this.expenseName = expenseName;
		this.owedAmount = owedAmount;
		this.createdUserName = createdUserName;
	}
	
	

}

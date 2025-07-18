package com.splitwise.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.splitwise.model.Expense;
import com.splitwise.model.ExpensePayer;
import com.splitwise.model.Split;
import com.splitwise.model.User;

import lombok.Data;

@Data
public class ExpenseDetailResponse {
	private int expenseId;
	private String description;
	private BigDecimal amount;
	private LocalDateTime createdAt;
	private int groupId;
	private User createdBy;
	private List<PayerDto> payers;
	private List<SplitDto> splits;
	public ExpenseDetailResponse(Expense expense, List<PayerDto> payers, List<SplitDto> splits) {
		this.expenseId = expense.getExpenseId();
		this.description = expense.getDescription();
		this.amount = expense.getAmount();
		this.createdAt = expense.getCreatedAt();
		this.groupId = expense.getGroup() != null ? expense.getGroup().getGroupId() : 0;
		this.createdBy = expense.getCreatedBy();
		this.createdAt = expense.getCreatedAt();
		this.payers = payers;
		this.splits = splits;
	}
	
	
	
}

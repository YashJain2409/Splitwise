package com.splitwise.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "expense_payer")
@Data
public class ExpensePayer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "expense_id")
	private Expense expense;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private BigDecimal amountPaid;
	
	
}

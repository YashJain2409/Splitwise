package com.splitwise.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserBalanceDTO {
	 private String otherUserName;
	 private BigDecimal amount;

	 public UserBalanceDTO(String otherUserName, BigDecimal amount) {
	        this.otherUserName = otherUserName;
	        this.amount = amount;
	 }

}

package com.splitwise.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SplitDto {
	int userId;
	String name;
	BigDecimal amount;
}

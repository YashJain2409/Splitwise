package com.splitwise.splitstrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.intfc.SplitStrategy;
import com.splitwise.model.Expense;
import com.splitwise.model.Split;
import com.splitwise.model.User;

public class UnequalSplitStrategy implements SplitStrategy {

	@Override
	public List<Split> calculateSplits(Expense expnese,List<CreateExpenseRequest.SplitDto> splitDtos) {
		// implement;
		return null;
	}

}

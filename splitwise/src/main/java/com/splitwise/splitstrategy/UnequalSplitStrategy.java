package com.splitwise.splitstrategy;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.intfc.SplitStrategy;
import com.splitwise.model.Expense;
import com.splitwise.model.Split;

import java.util.List;

public class UnequalSplitStrategy implements SplitStrategy {

	@Override
	public List<Split> calculateSplits(Expense expnese,List<CreateExpenseRequest.SplitDto> splitDtos) {
		// implement;
		return null;
	}

}

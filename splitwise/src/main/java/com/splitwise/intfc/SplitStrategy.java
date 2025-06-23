package com.splitwise.intfc;

import java.util.List;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.model.Expense;
import com.splitwise.model.Split;

public interface SplitStrategy {
	List<Split> calculateSplits(Expense expense, List<CreateExpenseRequest.SplitDto> splitDtos);
}

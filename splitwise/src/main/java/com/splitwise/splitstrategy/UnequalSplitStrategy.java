package com.splitwise.splitstrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.splitwise.intfc.SplitStrategy;
import com.splitwise.model.User;

public class UnequalSplitStrategy implements SplitStrategy {

	@Override
	public Map<User, BigDecimal> calculateSplits(BigDecimal totalAmount, List<User> participants,
			Map<User, BigDecimal> shares) {
		// implement;
		return null;
	}

}

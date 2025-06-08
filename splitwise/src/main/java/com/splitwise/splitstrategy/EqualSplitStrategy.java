package com.splitwise.splitstrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.splitwise.intfc.SplitStrategy;
import com.splitwise.model.User;

public class EqualSplitStrategy implements SplitStrategy {


	@Override
	public Map<User, BigDecimal> calculateSplits(BigDecimal totalAmount, List<User> participants,
			Map<User, BigDecimal> ignored) {
		int n = participants.size();
		Map<User,BigDecimal> shares = new HashMap<>();
		BigDecimal share = totalAmount.divide(BigDecimal.valueOf(n),2,RoundingMode.HALF_UP);
		for(User u : participants) {
			shares.put(u,share);
		}
		return shares;
	}
	
}

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
			Map<User, BigDecimal> userPaidMap,Map<User,BigDecimal> userOwedMap) {
		int n = participants.size();
		Map<User,BigDecimal> shares = new HashMap<>();
		BigDecimal share = totalAmount.divide(BigDecimal.valueOf(n),2,RoundingMode.HALF_UP);
		for(User u : participants) {
			BigDecimal paid = userPaidMap.getOrDefault(u, BigDecimal.ZERO);
			BigDecimal net = paid.subtract(share);
			shares.put(u,net);
		}
		return shares;
	}
	
}

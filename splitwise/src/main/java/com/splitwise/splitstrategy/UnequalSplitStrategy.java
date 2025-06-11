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
			Map<User, BigDecimal> userPaidMap,Map<User, BigDecimal> userOwedMap) {
		Map<User,BigDecimal> shares = new HashMap<User, BigDecimal>();
		for(User u : participants) {
			BigDecimal owed = userOwedMap.getOrDefault(u, BigDecimal.ZERO);
	        BigDecimal paid = userPaidMap.getOrDefault(u, BigDecimal.ZERO);
	        BigDecimal net = paid.subtract(owed);
	        shares.put(u,net);
		}
		return shares;
	}

}

package com.splitwise.intfc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.splitwise.model.User;

public interface SplitStrategy {
	Map<User,BigDecimal> calculateSplits(BigDecimal totalAmount, List<User> participants,
			Map<User, BigDecimal> userPaidMap,Map<User,BigDecimal> userOwedMap);
}

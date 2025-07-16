package com.splitwise.splitstrategy;

import com.splitwise.intfc.ExpenseStrategyFactory;
import com.splitwise.intfc.SplitStrategy;

public class UnEqualStrategyFactory extends ExpenseStrategyFactory {

	@Override
	public String getType() {
		return "UNEQUAL";
	}

	@Override
	public SplitStrategy createStrategy() {
		return new UnequalSplitStrategy();
	}

}

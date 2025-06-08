package com.splitwise.splitstrategy;

import org.springframework.stereotype.Component;

import com.splitwise.intfc.ExpenseStrategyFactory;
import com.splitwise.intfc.SplitStrategy;

@Component
public class EqualStrategyFactory extends ExpenseStrategyFactory {

	@Override
	public String getType() {
		return "EQUAL";
	}

	@Override
	public SplitStrategy createStrategy() {
		return new EqualSplitStrategy();
	}

}

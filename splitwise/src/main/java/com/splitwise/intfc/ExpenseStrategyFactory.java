package com.splitwise.intfc;

public abstract class ExpenseStrategyFactory {
	public abstract String getType();
	public abstract SplitStrategy createStrategy();
}

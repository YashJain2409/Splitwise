package com.splitwise.registry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.splitwise.intfc.ExpenseStrategyFactory;
import com.splitwise.intfc.SplitStrategy;

@Component
public class StrategyFactoryRegistry {

	private final Map<String, ExpenseStrategyFactory> factoryMap = new HashMap<>();
	 
	public StrategyFactoryRegistry(List<ExpenseStrategyFactory> factories) {
		for(ExpenseStrategyFactory factory : factories) {
			factoryMap.put(factory.getType().toUpperCase(), factory);
		}
	}
	public SplitStrategy getStrategy(String type) {
		ExpenseStrategyFactory expenseStrategyFactory = factoryMap.get(type);
		return expenseStrategyFactory.createStrategy();
	}
}

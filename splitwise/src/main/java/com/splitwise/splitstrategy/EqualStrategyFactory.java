package com.splitwise.splitstrategy;

import org.springframework.stereotype.Component;

import com.splitwise.intfc.ExpenseStrategyFactory;
import com.splitwise.intfc.SplitStrategy;
import com.splitwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EqualStrategyFactory extends ExpenseStrategyFactory {

	final UserRepository userRepository;
	
	@Override
	public String getType() {
		return "EQUAL";
	}

	@Override
	public SplitStrategy createStrategy() {
		return new EqualSplitStrategy(userRepository);
	}

}

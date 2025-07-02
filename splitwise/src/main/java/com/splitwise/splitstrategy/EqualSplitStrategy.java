package com.splitwise.splitstrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.intfc.SplitStrategy;
import com.splitwise.model.Expense;
import com.splitwise.model.Split;
import com.splitwise.model.User;
import com.splitwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;

public class EqualSplitStrategy implements SplitStrategy {
	
	UserRepository userRepository;
	
	public EqualSplitStrategy(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	

	@Override
	public List<Split> calculateSplits(Expense expnese,List<CreateExpenseRequest.SplitDto> splitDtos) {
		int n = splitDtos.size();
		BigDecimal share = expnese.getAmount().divide(BigDecimal.valueOf(n),2,RoundingMode.HALF_UP);
		return splitDtos.stream().map(s -> {
			Split split = new Split();
			split.setExpense(expnese);
			split.setAmount(share);
			split.setUser(userRepository.findById(s.getUserId()).orElseThrow());
			return split;
		}).toList();
	}


	
	
}

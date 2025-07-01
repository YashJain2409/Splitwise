package com.splitwise.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.splitwise.model.ExpensePayer;
import com.splitwise.model.Group;
import com.splitwise.model.Split;
import com.splitwise.model.User;

@Service
public class BalanceService {

	public void updateBalances(List<ExpensePayer> payers, List<Split> expenseSplit, Group group) {
		Map<User,BigDecimal> paid = new HashMap<>();
		Map<User,BigDecimal> owed = new HashMap<>();
		for(ExpensePayer p : payers) {
		    paid.merge(p.getUser(), p.getAmountPaid(), BigDecimal::add);
		}
		for(Split s : expenseSplit) {
			owed.merge(s.getUser(), s.getAmount(), BigDecimal::add);
		}
		
		// Net per user
        Map<User, BigDecimal> net = new HashMap<>();
        Set<User> all = new HashSet<>();
        all.addAll(paid.keySet());
        all.addAll(owed.keySet());

        for (User u : all) {
            BigDecimal userPaid = paid.getOrDefault(u, BigDecimal.ZERO);
            BigDecimal userOwes = owed.getOrDefault(u, BigDecimal.ZERO);
            net.put(u, userPaid.subtract(userOwes));
        }
        
        List<User> creditors = net.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0).map(
        		e -> e.getKey()).toList();
        
        List<User> debtors = net.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) < 0).map(e -> e.getKey()).toList();
        
        for(User debitor : debtors) {
        	BigDecimal toPay = net.get(debitor).negate();
        	for(User creditor : creditors) {
        		BigDecimal available = net.get(creditor);
        		if(available.compareTo(BigDecimal.ZERO) == 0) 
        			continue;
        		
        		BigDecimal settled = toPay.min(available);
        		
        		updateOrInsertBalance(group,debitor,creditor,settled);
        		toPay = toPay.subtract(settled);
        		if(toPay.compareTo(BigDecimal.ZERO) == 0)
        			break;
        	}
        	
        }
	}

	private void updateOrInsertBalance(Group group, User debitor, User creditor, BigDecimal settled) {
		
	}

}

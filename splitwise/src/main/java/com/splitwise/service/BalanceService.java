package com.splitwise.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.splitwise.controller.BalanceController;
import com.splitwise.dto.UserBalanceDTO;
import com.splitwise.model.ExpensePayer;
import com.splitwise.model.Group;
import com.splitwise.model.Split;
import com.splitwise.model.User;
import com.splitwise.model.UserBalance;
import com.splitwise.repository.UserBalanceRepository;
import com.splitwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {

	
	final UserBalanceRepository userBalanceRepository;
	final UserRepository userRepository;


	public void updateBalances(List<ExpensePayer> payers, List<Split> expenseSplit, Group group,boolean reverse) {
		Map<User,BigDecimal> paid = new HashMap<>();
		Map<User,BigDecimal> owed = new HashMap<>();
		System.out.println(payers.size());
		for(ExpensePayer p : payers) {
		    paid.merge(p.getUser(), p.getAmountPaid(), BigDecimal::add);
		    System.out.println("pay " + p.getAmountPaid());
		}
		System.out.println("split " + expenseSplit.size());
		for(Split s : expenseSplit) {
			owed.merge(s.getUser(), s.getAmount(), BigDecimal::add);
			System.out.println("split " + s.getAmount());
		}
		
		// Net per user
        Map<User, BigDecimal> net = new HashMap<>();
        Set<User> all = new HashSet<>();
        all.addAll(paid.keySet());
        all.addAll(owed.keySet());

        for (User u : all) {
            BigDecimal userPaid = paid.getOrDefault(u, BigDecimal.ZERO);
            BigDecimal userOwes = owed.getOrDefault(u, BigDecimal.ZERO);
            System.out.println("all " + userPaid + " " + userOwes);
            BigDecimal netAm = reverse ? userPaid.subtract(userOwes).negate() : userPaid.subtract(userOwes);
            System.out.println(netAm);
            net.put(u,reverse ? userPaid.subtract(userOwes).negate() : userPaid.subtract(userOwes));
        }
        
        List<User> creditors = net.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0).map(
        		e -> e.getKey()).toList();
        
        List<User> debtors = net.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) < 0).map(e -> e.getKey()).toList();
        System.out.println("size : " + debtors.size() + " " + creditors.size());
        for(User debitor : debtors) {
        	BigDecimal toPay = net.get(debitor).negate();
        	for(User creditor : creditors) {
        		BigDecimal available = net.get(creditor);
        		System.out.println("available amount " + reverse + " " + available);
        		if(available.compareTo(BigDecimal.ZERO) == 0) 
        			continue;
        		
        		BigDecimal settled = toPay.min(available);
        		System.out.println("available amount " + reverse + " " + available);
        		updateOrInsertBalance(group,debitor,creditor,settled,reverse);
        		toPay = toPay.subtract(settled);
        		if(toPay.compareTo(BigDecimal.ZERO) == 0)
        			break;
        	}
        	
        }
	}

	private void updateOrInsertBalance(Group group, User from, User to, BigDecimal delta,boolean reverse) {
		Optional<UserBalance> balance = userBalanceRepository.findByGroupAndFromUserAndToUser(group, from, to);
		if(balance.isPresent()) {
			UserBalance ub = balance.get();
	        BigDecimal newAmount = ub.getBalance().add(delta);
	        System.out.println("reverse : " + " " + reverse + " " + newAmount);
            if (newAmount.compareTo(BigDecimal.ZERO) == 0) {
                userBalanceRepository.delete(ub);
            } else {
                ub.setBalance(newAmount);
                userBalanceRepository.save(ub);
            }
		} else {
			   UserBalance ub = new UserBalance();
	            ub.setGroup(group);
	            ub.setFromUser(from);
	            ub.setToUser(to);
	            ub.setBalance(delta);
	            userBalanceRepository.save(ub);
		}
	}

	public List<UserBalanceDTO> getUserBalancesInGroup(int userId, int groupId) {
		User u = userRepository.findById(userId).orElseThrow();
		List<UserBalance> balances = userBalanceRepository.findBalancesForUserInGroup(userId, groupId);
		Map<String, BigDecimal> net = new HashMap<>();
		for(UserBalance b : balances) {

			String otherUserName;
			BigDecimal amount;
			
			if(b.getFromUser().getUserId() == userId) {
				otherUserName = b.getToUser().getName();
				amount = b.getBalance().negate();
			}else {
				otherUserName = b.getFromUser().getName();
				amount = b.getBalance();
			}
			
			net.put(otherUserName,net.getOrDefault(otherUserName, BigDecimal.ZERO).add(amount));
			
		}
		
		return net.entrySet().stream().map(e -> new UserBalanceDTO(e.getKey(), e.getValue())).toList();
	}

}

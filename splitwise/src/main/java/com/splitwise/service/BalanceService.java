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

	public void updateBalances(Map<User, BigDecimal> net, Group group, boolean reverse) {

		// change for reversal roles

		Map<User, BigDecimal> adjustedNet = new HashMap<>();
		for (Map.Entry<User, BigDecimal> entry : net.entrySet()) {
			BigDecimal value = reverse ? entry.getValue().negate() : entry.getValue();
			adjustedNet.put(entry.getKey(), value);
			System.out.println(entry.getKey().getName() + " " + value);
		}

		List<User> creditors = adjustedNet.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) > 0)
				.map(e -> e.getKey()).toList();

		List<User> debtors = adjustedNet.entrySet().stream().filter(e -> e.getValue().compareTo(BigDecimal.ZERO) < 0)
				.map(e -> e.getKey()).toList();
		for (User debitor : debtors) {
			BigDecimal toPay = adjustedNet.get(debitor).negate();
			System.out.println(toPay + " " + debitor.getName());
			for (User creditor : creditors) {
				BigDecimal available = adjustedNet.get(creditor);
				System.out.println(available + " " + creditor.getName());
				if (available.compareTo(BigDecimal.ZERO) == 0)
					continue;

				BigDecimal settled = toPay.min(available);
				updateOrInsertBalance(group, debitor, creditor, settled);
				toPay = toPay.subtract(settled);
				adjustedNet.put(creditor, available.subtract(settled));
				if (toPay.compareTo(BigDecimal.ZERO) == 0)
					break;
			}

		}

	}

	private void updateOrInsertBalance(Group group, User from, User to, BigDecimal delta) {
		Optional<UserBalance> balance = userBalanceRepository.findByGroupAndFromUserAndToUser(group, from, to);
		if(!balance.isPresent()) {
			balance = userBalanceRepository.findByGroupAndFromUserAndToUser(group,to,from);
			if(balance.isPresent())
				delta = delta.negate();
			System.out.println("inside present " + delta);
		}
		if (balance.isPresent()) {
			System.out.println(delta + " " + from.getName() + " " + to.getName());
			UserBalance ub = balance.get();
			BigDecimal newAmount = ub.getBalance().add(delta);
			System.out.println(newAmount + " " + newAmount.compareTo(BigDecimal.ZERO));
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
		for (UserBalance b : balances) {

			String otherUserName;
			BigDecimal amount;

			if (b.getFromUser().getUserId() == userId) {
				otherUserName = b.getToUser().getName();
				amount = b.getBalance().negate();
			} else {
				otherUserName = b.getFromUser().getName();
				amount = b.getBalance();
			}

			net.put(otherUserName, net.getOrDefault(otherUserName, BigDecimal.ZERO).add(amount));

		}

		return net.entrySet().stream().map(e -> new UserBalanceDTO(e.getKey(), e.getValue())).toList();
	}

	public List<UserBalanceDTO> getUserBalancesOutsideGroup(int userId) {
		User u = userRepository.findById(userId).orElseThrow();
		List<UserBalance> balances = userBalanceRepository.findBalancesForUserOutsideGroup(userId);
		Map<String, BigDecimal> net = new HashMap<>();
		for (UserBalance b : balances) {

			String otherUserName;
			BigDecimal amount;

			if (b.getFromUser().getUserId() == userId) {
				otherUserName = b.getToUser().getName();
				amount = b.getBalance().negate();
			} else {
				otherUserName = b.getFromUser().getName();
				amount = b.getBalance();
			}

			net.put(otherUserName, net.getOrDefault(otherUserName, BigDecimal.ZERO).add(amount));

		}

		return net.entrySet().stream().map(e -> new UserBalanceDTO(e.getKey(), e.getValue())).toList();
	}

}

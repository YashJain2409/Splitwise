package com.splitwise.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.splitwise.controller.BalanceController;
import com.splitwise.dto.TransactionDTO;
import com.splitwise.dto.UserBalanceDTO;
import com.splitwise.model.ExpensePayer;
import com.splitwise.model.Group;
import com.splitwise.model.Split;
import com.splitwise.model.User;
import com.splitwise.model.UserBalance;
import com.splitwise.repository.GroupRepository;
import com.splitwise.repository.UserBalanceRepository;
import com.splitwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

	final UserBalanceRepository userBalanceRepository;
	final UserRepository userRepository;
	final GroupRepository groupRepository;

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
		if (!balance.isPresent()) {
			balance = userBalanceRepository.findByGroupAndFromUserAndToUser(group, to, from);
			if (balance.isPresent())
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

	public List<TransactionDTO> getUserBalancesInGroup(int userId, int groupId) {
		User u = userRepository.findById(userId).orElseThrow();

		boolean simplifyDebt = groupRepository.findById(groupId).map(Group::isSimplifyDebt).orElse(false);

		List<UserBalance> allBalances = userBalanceRepository.findBalancesForGroup(groupId);

		Map<User, BigDecimal> net = new HashMap<>();
		for (UserBalance b : allBalances) {
			net.put(b.getFromUser(), net.getOrDefault(b.getFromUser(), BigDecimal.ZERO).subtract(b.getBalance()));
			net.put(b.getToUser(), net.getOrDefault(b.getToUser(), BigDecimal.ZERO).add(b.getBalance()));
			System.out.println(b.getFromUser() + " " + net.get(b.getFromUser()));
			System.out.println(b.getToUser() + " " + net.get(b.getToUser()));
			System.out.println(b.getFromUser() + " " + b.getToUser() + " " + b.getBalance());
		}

		if (!simplifyDebt) {
			return allBalances.stream().filter(b -> b.getFromUser().getEmail().equals(u.getEmail())
					|| b.getToUser().getEmail().equals(u.getEmail())).map(b -> {
						if (b.getFromUser().getEmail().equals(u.getEmail())) {
							return new TransactionDTO(u.getEmail(), b.getToUser().getEmail(), b.getBalance());
						} else {
							return new TransactionDTO(b.getFromUser().getEmail(), u.getEmail(), b.getBalance());
						}
					}).toList();
		}

		List<User> users = new ArrayList<>(net.keySet());
		for (User user : users) {
			System.out.println(user.getEmail());
		}
		List<BigDecimal> amounts = users.stream().map(user -> net.get(user))
				.collect(Collectors.toCollection(ArrayList::new));
		log.info("simplifify debit on : " + simplifyDebt);
		for (BigDecimal a : amounts) {
			System.out.println("amounts " + a);
		}
		List<TransactionDTO> simplifiedDebts = minimizeTransaction(users, amounts);
		for (TransactionDTO t : simplifiedDebts) {
			System.out.println(t.fromUser() + t.toUser() + t.amount());
		}
//		System.out.println("size " + simplifiedDebts.size))
		return simplifiedDebts.stream()
				.filter(tx -> tx.fromUser().equals(u.getEmail()) || tx.toUser().equals(u.getEmail())).toList();

	}

	private List<TransactionDTO> minimizeTransaction(List<User> users, List<BigDecimal> amounts) {
		List<TransactionDTO> res = new ArrayList<>();
		backtrack(users, amounts, 0, new ArrayList<>(), res);
		return res;
	}

	private void backtrack(List<User> users, List<BigDecimal> amounts, int start, List<TransactionDTO> currDebts,
			List<TransactionDTO> res) {
		while (start < users.size() && amounts.get(start).compareTo(BigDecimal.ZERO) == 0) {
			start++;
		}
		if (start == amounts.size()) {
			if (res.isEmpty() || currDebts.size() < res.size()) {
				for (int i = 0; i < currDebts.size(); i++) {
					log.info("curr ele : " + currDebts.get(i));
				}
				res.clear();
				res.addAll(currDebts);
			}
			return;
		}
		int min = Integer.MAX_VALUE;
		BigDecimal startBal = amounts.get(start);

		for (int i = start + 1; i < amounts.size(); i++) {
			// if one transaction is positive and another is negative then settle them
			if (startBal.signum() * amounts.get(i).signum() < 0) {
				System.out.println(users.get(i).getName() + " " + users.get(start).getName());

				amounts.set(i, amounts.get(i).add(startBal));
//				amounts.set(start, BigDecimal.ZERO);
				System.out.println(
						"amount after settlign " + i + " " + amounts.get(i) + " " + start + " " + amounts.get(start));
				String from = startBal.signum() < 0 ? users.get(start).getEmail() : users.get(i).getEmail();
				String to = startBal.signum() < 0 ? users.get(i).getEmail() : users.get(start).getEmail();

				currDebts.add(new TransactionDTO(from, to, amounts.get(start)));
				backtrack(users, amounts, start + 1, currDebts, res);

				currDebts.remove(currDebts.size() - 1);
//				amounts.set(start, startBal);
//				System.out.println("settlging " + amounts.get(i) + " " + amounts.get(start));
				amounts.set(i, amounts.get(i).subtract(startBal));
				System.out.println("amount after backtracking " + i + " " + amounts.get(i) + " " + start + " "
						+ amounts.get(start));
			}
		}
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

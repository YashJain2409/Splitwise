package com.splitwise.service;

import com.splitwise.dao.*;
import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.dto.CreateExpenseRequest.PayerDto;
import com.splitwise.dto.ExpenseDetailResponse;
import com.splitwise.dto.SplitDto;
import com.splitwise.dto.UserDTO;
import com.splitwise.enums.ExpenseSplitType;
import com.splitwise.enums.NotificationEventType;
import com.splitwise.events.ExpenseCreatedEvent;
import com.splitwise.events.ExpenseDeletedEvent;
import com.splitwise.events.ExpenseUpdatedEvent;
import com.splitwise.exception.ApplicationException;
import com.splitwise.intfc.SplitStrategy;
import com.splitwise.model.Expense;
import com.splitwise.model.ExpensePayer;
import com.splitwise.model.Group;
import com.splitwise.model.NotificationPreferences;
import com.splitwise.model.Split;
import com.splitwise.model.User;
import com.splitwise.registry.StrategyFactoryRegistry;
import com.splitwise.repository.ExpensePayerRepository;
import com.splitwise.repository.ExpenseRepository;
import com.splitwise.repository.GroupRepository;
import com.splitwise.repository.SplitRepository;
import com.splitwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseService {

	final StrategyFactoryRegistry strategyFactoryRegistry;
	final ExpensePayerRepository payerRepository;
	final ExpenseRepository expenseRepository;
	final SplitRepository splitRepository;
	final UserRepository userRepository;
	final BalanceService balanceService;
	final PayersDAO payersDAO;
	final UserDAO userDAO;
	final ModelMapper mapper;
	final GroupDAO groupDAO;
	final ExpenseDAO expenseDAO;
	final ModelMapper modelMapper;
	final SplitDAO splitDAO;
	final NotificationsProducer notificationsProducer;

	@Transactional
	public void createExpense(CreateExpenseRequest expenseReq) {

		// validate payment
		BigDecimal sumOfPayerAmounts = expenseReq.getPayers().stream().map(PayerDto::getAmountPaid)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (sumOfPayerAmounts.compareTo(expenseReq.getAmount()) != 0) {
			throw new IllegalArgumentException("Sum of payer amounts must equal total expense amount");
		}
		UserDTO userDetails = userDAO.findById(expenseReq.getCreatedBy());

		User createdBy = mapper.map(userDetails, User.class);
		// Save expense
		Expense expense = new Expense();
		Group group = null;
		if (expenseReq.getGroupId() != null) {
			group = groupDAO.findById(expenseReq.getGroupId());
			expense.setGroup(group);
		}
		expense.setCreatedBy(createdBy);
		expense.setUpdatedBy(createdBy);
		expense.setCreatedAt(LocalDateTime.now());
		expense.setExpenseSplitType(ExpenseSplitType.valueOf(expenseReq.getSplitType()));
		expense.setAmount(expenseReq.getAmount());
		expense.setDescription(expenseReq.getDescription());
		Expense savedExpense = expenseDAO.saveExpense(expense);

		// save payers
		List<ExpensePayer> payers = expenseReq.getPayers().stream().map(p -> {
			ExpensePayer payer = new ExpensePayer();
			payer.setExpense(savedExpense);
			payer.setUser(modelMapper.map(userDAO.findById(p.getUserId()), User.class));
			payer.setAmountPaid(p.getAmountPaid());
			return payer;
		}).toList();
		payersDAO.saveExpensePayers(payers);
		SplitStrategy strategy = strategyFactoryRegistry.getStrategy(expenseReq.getSplitType());
		List<Split> expenseSplit = strategy.calculateSplits(expense, expenseReq.getSplits());
		splitDAO.saveSplits(expenseSplit);
		// update balance
		Map<User, BigDecimal> net = createNetMoneyForUser(payers, expenseSplit);
		balanceService.updateBalances(net, group, false);

		for (Map.Entry<User, BigDecimal> recipients : net.entrySet()) {
			if (recipients.getKey().getUserId() == expense.getCreatedBy().getUserId())
				continue;
			ExpenseCreatedEvent expenseCreatedEvent = new ExpenseCreatedEvent(expense.getDescription(),
					expense.getAmount(), recipients.getValue(), recipients.getKey(), expense.getCreatedBy().getName());

			notificationsProducer.sendEvent(expenseCreatedEvent);
		}

	}

	private Map<User, BigDecimal> createNetMoneyForUser(List<ExpensePayer> payers, List<Split> expenseSplit) {
		Map<User, BigDecimal> paid = new HashMap<>();
		Map<User, BigDecimal> owed = new HashMap<>();
		for (ExpensePayer p : payers) {
			paid.merge(p.getUser(), p.getAmountPaid(), BigDecimal::add);
		}
		for (Split s : expenseSplit) {
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
		return net;
	}

	public void deleteExpense(int expenseId, String username) {
		User user = userRepository.findByEmail(username);
		Expense e = expenseDAO.findExpenseById(expenseId);
		if (e.getDeleted() == true)
			throw new ApplicationException("expense already deleted", HttpStatus.BAD_REQUEST);
		e.setDeleted(true);
		List<ExpensePayer> payers = e.getPayers();
		List<Split> split = e.getSplits();
		Map<User, BigDecimal> net = createNetMoneyForUser(payers, split);
		Group g = e.getGroup();
		expenseDAO.saveExpense(e);
		// TODO: update net balances for users.
		balanceService.updateBalances(net, g, true);
		for (Map.Entry<User, BigDecimal> recipients : net.entrySet()) {
			if (user.getUserId() == recipients.getKey().getUserId())
				continue;
			System.out.println(recipients.getKey().getName());
			ExpenseDeletedEvent expenseDeletedEvent = new ExpenseDeletedEvent(e.getDescription(), e.getAmount(),
					recipients.getValue(), recipients.getKey(), user.getName());

			notificationsProducer.sendEvent(expenseDeletedEvent);
		}
	}

	@Transactional
	public void updateExpense(CreateExpenseRequest expenseReq, int expenseId) {
		BigDecimal sumOfPayerAmounts = expenseReq.getPayers().stream().map(PayerDto::getAmountPaid)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		if (sumOfPayerAmounts.compareTo(expenseReq.getAmount()) != 0) {
			throw new IllegalArgumentException("Sum of payer amounts must equal total expense amount");
		}

		Expense ex = expenseRepository.findById(expenseId).orElseThrow();
		UserDTO userDetails = userDAO.findById(expenseReq.getUpdatedBy());

		User updatedBy = mapper.map(userDetails, User.class);

		Group g = ex.getGroup();
		if (expenseReq.getGroupId() != null && expenseReq.getGroupId() != g.getGroupId())
			throw new IllegalArgumentException("Something went wrong");

		Map<User, BigDecimal> net = createNetMoneyForUser(ex.getPayers(), ex.getSplits());
		System.out.println("reverting old expense");
		balanceService.updateBalances(net, g, true);

		splitRepository.deleteByExpenseId(ex.getExpenseId());
		payerRepository.deleteByExpenseId(ex.getExpenseId());

		ex.setDescription(expenseReq.getDescription());
		ex.setAmount(expenseReq.getAmount());
		ex.setUpdatedBy(updatedBy);
		ex.setExpenseSplitType(ExpenseSplitType.valueOf(expenseReq.getSplitType()));
		ex.getPayers().clear();
		ex.getSplits().clear();
		expenseRepository.save(ex);

		// save payers
		List<ExpensePayer> payers = expenseReq.getPayers().stream().map(p -> {
			ExpensePayer payer = new ExpensePayer();
			payer.setExpense(ex);
			payer.setUser(userRepository.findById(p.getUserId()).orElseThrow());
			payer.setAmountPaid(p.getAmountPaid());
			return payer;
		}).toList();
		payerRepository.saveAll(payers);
		SplitStrategy strategy = strategyFactoryRegistry.getStrategy(expenseReq.getSplitType());
		List<Split> expenseSplit = strategy.calculateSplits(ex, expenseReq.getSplits());
		splitRepository.saveAll(expenseSplit);

		Map<User, BigDecimal> netMoney = createNetMoneyForUser(payers, expenseSplit);

		System.out.println("adding new expense");
		balanceService.updateBalances(netMoney, g, false);

		for (Map.Entry<User, BigDecimal> recipients : netMoney.entrySet()) {
			if (recipients.getKey().getUserId() == ex.getUpdatedBy().getUserId())
				continue;
			ExpenseUpdatedEvent expenseUpdatedEvent = new ExpenseUpdatedEvent(ex.getDescription(), ex.getAmount(),
					recipients.getValue(), recipients.getKey(), ex.getUpdatedBy().getName());

			notificationsProducer.sendEvent(expenseUpdatedEvent);
		}

	}

	@Transactional
	public List<ExpenseDetailResponse> getPersonalExpenses(int userId) {
		List<Expense> expenses = expenseDAO.findAllPersonalExpenses(userId);
		return expenses.stream().map(e -> {
			List<com.splitwise.dto.PayerDto> payers = e.getPayers().stream()
					.map(p -> new com.splitwise.dto.PayerDto(p.getUser().getUserId(), p.getUser().getName(),
							p.getAmountPaid()))
					.toList();
			List<SplitDto> splits = e.getSplits().stream()
					.map(s -> new SplitDto(s.getUser().getUserId(), s.getUser().getName(), s.getAmount())).toList();

			return new ExpenseDetailResponse(e, payers, splits);
		}).toList();
	}

	@Transactional
	public List<ExpenseDetailResponse> getGroupExpenses(int groupId) {
		Group g = groupDAO.findById(groupId);
		List<Expense> expenses = expenseRepository.findByGroupAndDeletedFalse(g);
		return expenses.stream().map(e -> {
			List<com.splitwise.dto.PayerDto> payers = e.getPayers().stream()
					.map(p -> new com.splitwise.dto.PayerDto(p.getUser().getUserId(), p.getUser().getName(),
							p.getAmountPaid()))
					.toList();
			List<SplitDto> splits = e.getSplits().stream()
					.map(s -> new SplitDto(s.getUser().getUserId(), s.getUser().getName(), s.getAmount())).toList();

			return new ExpenseDetailResponse(e, payers, splits);
		}).toList();
	}

}

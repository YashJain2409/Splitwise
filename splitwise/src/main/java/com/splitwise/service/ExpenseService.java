package com.splitwise.service;

import com.splitwise.dao.*;
import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.dto.CreateExpenseRequest.PayerDto;
import com.splitwise.dto.ExpenseDetailResponse;
import com.splitwise.dto.SplitDto;
import com.splitwise.dto.UserDTO;
import com.splitwise.enums.ExpenseSplitType;
import com.splitwise.exception.ApplicationException;
import com.splitwise.intfc.SplitStrategy;
import com.splitwise.model.Expense;
import com.splitwise.model.ExpensePayer;
import com.splitwise.model.Group;
import com.splitwise.model.Split;
import com.splitwise.model.User;
import com.splitwise.registry.StrategyFactoryRegistry;
import com.splitwise.repository.ExpensePayerRepository;
import com.splitwise.repository.ExpenseRepository;
import com.splitwise.repository.GroupRepository;
import com.splitwise.repository.SplitRepository;
import com.splitwise.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    final StrategyFactoryRegistry strategyFactoryRegistry;
    final ExpensePayerRepository payerRepository;
    final BalanceService balanceService;
    final PayersDAO payersDAO;
    final UserDAO userDAO;
    final ModelMapper mapper;
    final GroupDAO groupDAO;
    final ExpenseDAO expenseDAO;
    final ModelMapper modelMapper;
    final SplitDAO splitDAO;
    
    
    @Transactional
    public void createExpense(CreateExpenseRequest expenseReq) {
    	
    	// validate payment
    	BigDecimal sumOfPayerAmounts = expenseReq.getPayers().stream()
    		    .map(PayerDto::getAmountPaid)
    		    .reduce(BigDecimal.ZERO, BigDecimal::add);

    		if (sumOfPayerAmounts.compareTo(expenseReq.getAmount()) != 0) {
    		    throw new IllegalArgumentException("Sum of payer amounts must equal total expense amount");
    		}
        UserDTO userDetails = userDAO.findById(expenseReq.getCreatedBy());

        User createdBy = mapper.map(userDetails,User.class);
     // Save expense
        Expense expense = new Expense();
        Group group = null;
        if (expenseReq.getGroupId() != null) {
            group = groupDAO.findById(expenseReq.getGroupId());
            expense.setGroup(group);
        }
        expense.setCreatedBy(createdBy);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setExpenseSplitType(ExpenseSplitType.valueOf(expenseReq.getSplitType()));
        expense.setAmount(expenseReq.getAmount());
        expense.setDescription(expenseReq.getDescription());
        Expense savedExpense = expenseDAO.saveExpense(expense);
        
        // save payers
        List<ExpensePayer> payers = expenseReq.getPayers().stream().map(p -> {
        	ExpensePayer payer = new ExpensePayer();
        	payer.setExpense(savedExpense);
        	payer.setUser(modelMapper.map(userDAO.findById(p.getUserId()),User.class));
        	payer.setAmountPaid(p.getAmountPaid());
        	return payer;
        }).toList();
        payersDAO.saveExpensePayers(payers);
        SplitStrategy strategy =  strategyFactoryRegistry.getStrategy(expenseReq.getSplitType());
        List<Split> expenseSplit = strategy.calculateSplits(expense, expenseReq.getSplits());
        splitDAO.saveSplits(expenseSplit);
        
        // update balance
        balanceService.updateBalances(payers,expenseSplit,group);
        
    }

    @Transactional
    public void deleteExpense(int expenseId) {
        Expense e = expenseDAO.findExpenseById(expenseId);
        e.setDeleted(true);
        expenseDAO.saveExpense(e);
    }

    public ResponseEntity<List<Expense>> getExpenseByFriendId(Integer friendId,int userId) {
//        List<Expense> expenses = expenseRepository.getExpenseByFriendId(friendId,userId);
//        if(expenses.isEmpty())
//            throw new ApplicationException("0000","No expenses found", HttpStatus.NOT_FOUND);
//        return new ResponseEntity<>(expenses,HttpStatus.OK);
        return null;
    }

    @Transactional
    public ResponseEntity<Expense> updateExpense(Expense expense,int expenseId) {
        Expense existExpense = expenseDAO.findExpenseById(expenseId);

        splitDAO.deleteSplitDetails(expenseId);
        expense.setExpenseId(existExpense.getExpenseId());
//        expense.setCreatedBy(1);
        expense =  expenseDAO.saveExpense(expense);

        return new ResponseEntity<>(expense,HttpStatus.OK);
    }

    public ResponseEntity<List<Expense>> findExpenseByGroupId(Integer groupId) {
//        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
//        if(expenses.isEmpty())
//            throw new ApplicationException("0000","No expenses found", HttpStatus.NOT_FOUND);
//        return new ResponseEntity<>(expenses,HttpStatus.OK);
    	return null;
    }

    @Transactional
	public List<ExpenseDetailResponse> getPersonalExpenses(int userId) {
		List<Expense> expenses = expenseDAO.findAllPersonalExpenses(userId);
		return expenses.stream().map(e -> {
			List<com.splitwise.dto.PayerDto> payers = e.getPayers().stream().map(p -> new com.splitwise.dto.PayerDto(p.getUser().getUserId(), p.getUser().getName(), p.getAmountPaid())).toList();
			List<SplitDto> splits = e.getSplits().stream().map(s -> new SplitDto(s.getUser().getUserId() ,s.getUser().getName() , s.getAmount())).toList();
		
			return new ExpenseDetailResponse(e, payers, splits);
		}).toList();
	}

    @Transactional
	public List<ExpenseDetailResponse> getGroupExpenses(int groupId) {
		Group g = groupDAO.findById(groupId);
		List<Expense> expenses = expenseDAO.findByGroup(g);
		return expenses.stream().map(e -> {
			List<com.splitwise.dto.PayerDto> payers = e.getPayers().stream().map(p -> new com.splitwise.dto.PayerDto(p.getUser().getUserId(), p.getUser().getName(), p.getAmountPaid())).toList();
			List<SplitDto> splits = e.getSplits().stream().map(s -> new SplitDto(s.getUser().getUserId() ,s.getUser().getName() , s.getAmount())).toList();
		
			return new ExpenseDetailResponse(e, payers, splits);
		}).toList();
	}
}

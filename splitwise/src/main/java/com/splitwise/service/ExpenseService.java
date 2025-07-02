package com.splitwise.service;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.dto.CreateExpenseRequest.PayerDto;
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


    final ExpenseRepository expenseRepository;
    final SplitRepository splitRepository;
    final UserRepository userRepository;
    final GroupRepository groupRepository;
    final StrategyFactoryRegistry strategyFactoryRegistry;
    final ExpensePayerRepository payerRepository;
    final BalanceService balanceService;
    
    

    public void createExpense(CreateExpenseRequest expenseReq) {
//    	System.out.println(expenseReq.getCreatedByUserId());
    	
    	// validate payment
    	BigDecimal sumOfPayerAmounts = expenseReq.getPayers().stream()
    		    .map(PayerDto::getAmountPaid)
    		    .reduce(BigDecimal.ZERO, BigDecimal::add);

    		if (sumOfPayerAmounts.compareTo(expenseReq.getAmount()) != 0) {
    		    throw new IllegalArgumentException("Sum of payer amounts must equal total expense amount");
    		}
        User createdBy = userRepository.findById(expenseReq.getCreatedBy()).orElseThrow();
        
     // Save expense
        Expense expense = new Expense();
        Group group = null;
        if (expenseReq.getGroupId() != null) {
            group = groupRepository.findById(expenseReq.getGroupId()).orElseThrow();
            expense.setGroup(group);
        }
        expense.setCreatedBy(createdBy);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setAmount(expenseReq.getAmount());
        expense.setDescription(expenseReq.getDescription());
        expenseRepository.save(expense);
        
        // save payers
        List<ExpensePayer> payers = expenseReq.getPayers().stream().map(p -> {
        	ExpensePayer payer = new ExpensePayer();
        	payer.setExpense(expense);
        	payer.setUser(userRepository.findById(p.getUserId()).orElseThrow());
        	payer.setAmountPaid(p.getAmountPaid());
        	return payer;
        }).toList();
        payerRepository.saveAll(payers);
        SplitStrategy strategy =  strategyFactoryRegistry.getStrategy(expenseReq.getSplitType());
        List<Split> expenseSplit = strategy.calculateSplits(expense, expenseReq.getSplits()); 
        splitRepository.saveAll(expenseSplit);
        
        // update balance
        balanceService.updateBalances(payers,expenseSplit,group);
        
    }

    public ResponseEntity<String> deleteExpense(int expenseId) {
        boolean isFound = expenseRepository.existsById(expenseId);
        if(!isFound) {
            throw new ApplicationException("0000","Expense not found", HttpStatus.NOT_FOUND);
        }
        expenseRepository.deleteById(expenseId);
        return new ResponseEntity<>("SUCCESS",HttpStatus.OK);
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
        Expense existExpense = expenseRepository.findById(expenseId).orElse(null);
        if(existExpense == null)
            throw new ApplicationException("0000","Invalid Expense", HttpStatus.NOT_FOUND);
        splitRepository.deleteSplitDetails(expenseId);
        expense.setExpenseId(existExpense.getExpenseId());
//        expense.setCreatedBy(1);
        return new ResponseEntity<>(expenseRepository.save(expense),HttpStatus.OK);
    }

    public ResponseEntity<List<Expense>> findExpenseByGroupId(Integer groupId) {
//        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
//        if(expenses.isEmpty())
//            throw new ApplicationException("0000","No expenses found", HttpStatus.NOT_FOUND);
//        return new ResponseEntity<>(expenses,HttpStatus.OK);
    	return null;
    }
}

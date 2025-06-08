package com.splitwise.service;

import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Expense;
import com.splitwise.model.Split;
import com.splitwise.repository.ExpenseRepository;
import com.splitwise.repository.SplitRepository;
import com.splitwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    @Autowired
    ExpenseRepository expenseRepository;
    @Autowired
    SplitRepository splitRepository;
    public Expense createExpense(Expense expense) {
        return expenseRepository.save(expense);
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
        List<Expense> expenses = expenseRepository.getExpenseByFriendId(friendId,userId);
        if(expenses.isEmpty())
            throw new ApplicationException("0000","No expenses found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(expenses,HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Expense> updateExpense(Expense expense,int expenseId) {
        Expense existExpense = expenseRepository.findById(expenseId).orElse(null);
        if(existExpense == null)
            throw new ApplicationException("0000","Invalid Expense", HttpStatus.NOT_FOUND);
        splitRepository.deleteSplitDetails(expenseId);
        expense.setExpenseId(existExpense.getExpenseId());
        expense.setCreatedBy(1);
        return new ResponseEntity<>(expenseRepository.save(expense),HttpStatus.OK);
    }

    public ResponseEntity<List<Expense>> findExpenseByGroupId(Integer groupId) {
        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        if(expenses.isEmpty())
            throw new ApplicationException("0000","No expenses found", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(expenses,HttpStatus.OK);
    }
}

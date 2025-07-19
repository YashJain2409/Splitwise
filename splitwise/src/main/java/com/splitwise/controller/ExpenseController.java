package com.splitwise.controller;

import com.splitwise.dto.CreateExpenseRequest;
import com.splitwise.dto.EmailNotification;
import com.splitwise.dto.ExpenseDetailResponse;
import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Expense;
import com.splitwise.model.Split;
import com.splitwise.service.ExpenseService;
import com.splitwise.service.NotificationsProducer;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Expense")
@RequiredArgsConstructor
public class ExpenseController {

    final ExpenseService expenseService;
    
    
    final NotificationsProducer notificationsProducer;
    
    
    
    @PostMapping("/Create")
    public ResponseEntity<String> createExpense(@RequestBody CreateExpenseRequest expense) {
        expenseService.createExpense(expense);
        return ResponseEntity.status(HttpStatus.CREATED).body("Expense created successfully");
    }


    @DeleteMapping("/{expenseId}")
    public ResponseEntity<String> deleteExpense(@PathVariable int expenseId) {
        expenseService.deleteExpense(expenseId);
        return ResponseEntity.ok("Expense deleted successfully");
    }

    @PostMapping("/Update/{expenseId}")
    public ResponseEntity<Expense> updateExpense(@RequestBody Expense expense, @PathVariable int expenseId) {
        if(expense.getDescription() == null || expense.getDescription().isEmpty())
            throw new ApplicationException("0000","Enter Description", HttpStatus.BAD_REQUEST);
//        if(expense.getAmount() == 0)
//            throw new ApplicationException("0001","Enter Amount",HttpStatus.BAD_REQUEST);
        return null;
//        String validSplitMessage = validateSplit(expense.getSplitDetails(),expense.getAmount());
//        if(validSplitMessage.equals("Success")){
//            return expenseService.updateExpense(expense,expenseId);
//        }
//        throw new ApplicationException("0002",validSplitMessage,HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{userId}/personal")
    public ResponseEntity<List<ExpenseDetailResponse>> getExpenseByFilter(@PathVariable int userId) {
        List<ExpenseDetailResponse> expenses = expenseService.getPersonalExpenses(userId);
    	return ResponseEntity.ok(expenses);
    }
    
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<List<ExpenseDetailResponse>> getGroupExpenses(@PathVariable int groupId) {
    	List<ExpenseDetailResponse> expenses = expenseService.getGroupExpenses(groupId);
    	return ResponseEntity.ok(expenses);
    }


}

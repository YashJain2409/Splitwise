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

import java.security.Principal;
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
	public ResponseEntity<String> deleteExpense(@PathVariable int expenseId, Principal principal) {
		expenseService.deleteExpense(expenseId, principal.getName());
		return ResponseEntity.ok("Expense deleted successfully");
	}

	@PostMapping("/Update/{expenseId}")
	public ResponseEntity<String> updateExpense(@RequestBody CreateExpenseRequest expenseReq,
			@PathVariable int expenseId) {
		expenseService.updateExpense(expenseReq, expenseId);
		return ResponseEntity.ok("Expense updated succesfully");
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

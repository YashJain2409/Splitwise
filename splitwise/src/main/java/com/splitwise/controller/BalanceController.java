package com.splitwise.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.splitwise.dto.TransactionDTO;
import com.splitwise.dto.UserBalanceDTO;
import com.splitwise.service.BalanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor
public class BalanceController {

	final BalanceService balanceService;

	@GetMapping("/{userId}/group/{groupId}")
	public ResponseEntity<List<TransactionDTO>> getUserBalanceInGroup(@PathVariable int userId,
			@PathVariable int groupId) {
		List<TransactionDTO> balances = balanceService.getUserBalancesInGroup(userId, groupId);
		return ResponseEntity.ok(balances);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<List<UserBalanceDTO>> getUserBalanceOutsideGroup(@PathVariable int userId) {
		List<UserBalanceDTO> balances = balanceService.getUserBalancesOutsideGroup(userId);
		return ResponseEntity.ok(balances);
	}
}

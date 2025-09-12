package com.splitwise.dto;

import java.math.BigDecimal;

public record TransactionDTO(String fromUser, String toUser, BigDecimal amount) {

}

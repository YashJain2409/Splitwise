package com.splitwise.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CreateExpenseRequest {
	private String description;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private Integer groupId;
    private int createdByUserId;
    private String splitType; // EQUAL, UNEQUAL, PERCENTAGE

    private List<PayerDto> payers;
    private List<SplitDto> splits;

    @Data
    public static class PayerDto {
        private int userId;
        private BigDecimal amountPaid;
    }

    @Data
    public static class SplitDto {
        private int userId;
        private BigDecimal amountOrPercentage;
    }
}

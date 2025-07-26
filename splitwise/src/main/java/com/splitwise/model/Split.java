package com.splitwise.model;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "split")
public class Split {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "split_id")
    private int splitId;
    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "percentage")
    private BigDecimal percentage;
}

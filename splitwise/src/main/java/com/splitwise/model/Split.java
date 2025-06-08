package com.splitwise.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "split")
public class Split {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "split_id")
    private int splitId;
    @Column(name = "paid_share")
    private double paidShare;
    @Column(name = "owe_share")
    private double oweShare;
    @OneToOne
    @JoinColumn(name = "user_id",insertable = false,updatable = false)
    private User user;
//    @Column(name = "expense_id")
//    private int expenseId;
    @Column(name = "user_id")
    private int userId;
}

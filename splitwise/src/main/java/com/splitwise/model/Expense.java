package com.splitwise.model;

import com.splitwise.enums.ExpenseSplitType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.util.List;

@Data
@Entity(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private int expenseId;
    @Column(name = "description")
    private String description;
    @Column(name = "amount")
    private double amount;
    @Column(name = "split_type")
    @Enumerated(EnumType.STRING)
    private ExpenseSplitType expenseSplitType;
    
    @OneToMany(mappedBy = "expense")
    private List<ExpensePayer> expensePayers;
    
    @OneToMany(mappedBy = "expense")
    private List<ExpenseParticipant> expenseParticipants;
    
    @Column(name = "created_by")
    private int createdBy;
}

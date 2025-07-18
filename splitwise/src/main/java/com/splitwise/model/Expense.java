package com.splitwise.model;

import com.splitwise.enums.ExpenseSplitType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Cascade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id")
    private int expenseId;
    @Column(name = "description")
    private String description;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "deleted")
    private Boolean deleted = false;
    @Column(name = "split_type")
    @Enumerated(EnumType.STRING)
    private ExpenseSplitType expenseSplitType;
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group; 
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<ExpensePayer> payers;
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL)
    private List<Split> splits;

    
}

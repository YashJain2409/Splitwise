package com.splitwise.dao;

import com.splitwise.exception.ApplicationException;
import com.splitwise.model.Expense;
import com.splitwise.model.Group;
import com.splitwise.repository.ExpenseRepository;
import com.splitwise.repository.GroupRepository;
import com.splitwise.repository.SplitRepository;
import com.splitwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExpenseDAO {

    final ExpenseRepository expenseRepository;
    final SplitRepository splitRepository;
    final UserRepository userRepository;
    final GroupRepository groupRepository;


    public Expense saveExpense(Expense expense){

        try{

            return expenseRepository.save(expense);
        }
        catch (Exception e){
            throw new ApplicationException("Something went wrong while create the expense", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Expense findExpenseById(int expenseId){

        return expenseRepository.findById(expenseId).orElseThrow(() -> new ApplicationException("Expense is not found for id :: " + expenseId,HttpStatus.NOT_FOUND));
    }

    public List<Expense> findAllPersonalExpenses(int userId){

        return expenseRepository.findAllPersonalExpenses(userId);
    }

    
}

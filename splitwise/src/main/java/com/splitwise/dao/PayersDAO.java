package com.splitwise.dao;

import com.splitwise.exception.ApplicationException;
import com.splitwise.model.ExpensePayer;
import com.splitwise.repository.ExpensePayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PayersDAO {

    @Autowired
    private ExpensePayerRepository expensePayerRepo;

    public List<ExpensePayer> saveExpensePayers(List<ExpensePayer> expensePayer){

        try{
            return (List<ExpensePayer>) expensePayerRepo.saveAll(expensePayer);
        }catch (Exception e){
            throw new ApplicationException("Something went wrong while add expense", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

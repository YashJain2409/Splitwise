package com.splitwise.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.splitwise.model.ExpensePayer;

@Repository
public interface ExpensePayerRepository extends CrudRepository<ExpensePayer, Long> {

}

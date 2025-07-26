package com.splitwise.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.splitwise.model.Expense;
import com.splitwise.model.ExpensePayer;

@Repository
public interface ExpensePayerRepository extends CrudRepository<ExpensePayer, Long> {


	@Modifying
	@Transactional
	@Query("DELETE FROM ExpensePayer s WHERE s.expense.id = :expenseId")
	void deleteByExpenseId(@Param("expenseId") int expenseId);

}

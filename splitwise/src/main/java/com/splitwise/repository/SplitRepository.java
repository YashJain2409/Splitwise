package com.splitwise.repository;

import com.splitwise.model.Expense;
import com.splitwise.model.Split;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface SplitRepository extends CrudRepository<Split,Integer> {


	@Modifying
	@Transactional
	@Query("DELETE FROM Split s WHERE s.expense.id = :expenseId")
	void deleteByExpenseId(int expenseId);
}

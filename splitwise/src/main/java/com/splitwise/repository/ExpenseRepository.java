package com.splitwise.repository;

import com.splitwise.model.Expense;
import com.splitwise.model.Group;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense,Integer> {

	@Query("select e from Expense e join e.splits s where e.group is null and s.user.id=:userId")
	List<Expense> findAllPersonalExpenses(int userId);


	List<Expense> findByGroupAndDeletedFalse(Group g);
	
		
}

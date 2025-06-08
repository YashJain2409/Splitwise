package com.splitwise.registry;

import com.splitwise.model.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends CrudRepository<Expense,Integer> {

    @Query("select e from expense e where (e.createdBy=:id and e.friendUserId=:friendId) or (e.createdBy=:friendId and e.friendUserId=:id)")
    List<Expense> getExpenseByFriendId(Integer friendId, int id);

    List<Expense> findByGroupId(Integer groupId);
}

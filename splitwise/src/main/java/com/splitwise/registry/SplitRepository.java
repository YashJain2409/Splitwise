package com.splitwise.registry;

import com.splitwise.model.Split;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface SplitRepository extends CrudRepository<Split,Integer> {

    @Modifying
    @Query(nativeQuery = true,value = "delete from split where split.expense_id = :expenseId")
    void deleteSplitDetails(int expenseId);
}

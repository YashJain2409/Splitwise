package com.splitwise.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.splitwise.model.Group;
import com.splitwise.model.User;
import com.splitwise.model.UserBalance;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance,Integer>{
	
	Optional<UserBalance> findByGroupAndFromUserAndToUser(Group group, User from, User to);

	@Query("select ub from UserBalance ub where ub.group.groupId = :groupId and (ub.fromUser.userId = :userId or ub.toUser.userId = :userId)")
	List<UserBalance> findBalancesForUserInGroup(@Param("userId") int userId, @Param("groupId") int groupId);
}

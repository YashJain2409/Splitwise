package com.splitwise.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.splitwise.model.Group;
import com.splitwise.model.User;
import com.splitwise.model.UserBalance;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance,Integer>{

	Optional<UserBalance> findByGroupAndFromUserAndToUser(Group group, User from, User to);
}

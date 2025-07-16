package com.splitwise.repository;

import com.splitwise.model.Group;
import com.splitwise.model.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<Group,Integer> {

	
}

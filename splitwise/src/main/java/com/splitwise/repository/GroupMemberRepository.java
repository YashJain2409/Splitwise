package com.splitwise.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.splitwise.model.Group;
import com.splitwise.model.GroupMember;
import com.splitwise.model.User;

public interface GroupMemberRepository extends CrudRepository<GroupMember, Integer> {
	public boolean existsByGroupAndUser(Group g,User u);
	
	public List<GroupMember> findByGroup(Group g);

	public GroupMember findByGroupAndUser(Group g, User u);

	public List<GroupMember> findByUser(User u);
}	

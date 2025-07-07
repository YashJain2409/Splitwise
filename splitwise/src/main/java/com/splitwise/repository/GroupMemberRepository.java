package com.splitwise.repository;

import org.springframework.data.repository.CrudRepository;

import com.splitwise.model.GroupMember;

public interface GroupMemberRepository extends CrudRepository<GroupMember, Integer> {

}

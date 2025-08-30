package com.splitwise.repository;

import com.splitwise.model.User;

import org.apache.kafka.common.quota.ClientQuotaAlteration.Op;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

	User findByEmail(String username);

}

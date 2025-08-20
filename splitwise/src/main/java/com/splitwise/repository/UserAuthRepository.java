package com.splitwise.repository;

import com.splitwise.model.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuth,Long> {

    UserAuth findByUserEmail(String email);
}

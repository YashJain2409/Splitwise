package com.splitwise.dao;

import com.splitwise.repository.ExpenseRepository;
import com.splitwise.repository.GroupRepository;
import com.splitwise.repository.SplitRepository;
import com.splitwise.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenseDAO {

    final ExpenseRepository expenseRepository;
    final SplitRepository splitRepository;
    final UserRepository userRepository;
    final GroupRepository groupRepository;

}

package com.splitwise.dto;

import com.splitwise.model.Group;
import com.splitwise.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.util.List;

public class GroupMemberDTO {

    private int groupId;

    private List<UserDTO> user;
}

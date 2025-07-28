package com.splitwise.dto;

import com.splitwise.model.GroupMember;
import com.splitwise.model.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupDTO {

    private int groupId;
    private String groupName;
    private UserDTO createdBy;
    private LocalDateTime createdAt;
    private List<GroupMemberDTO> members;
}
